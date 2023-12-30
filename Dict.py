import sys
import os
import logging
from azure.kusto.data import KustoClient, KustoConnectionStringBuilder
from azure.kusto.data.exceptions import KustoServiceError

# Set up logging
logging.basicConfig(filename='kusto_script.log', level=logging.DEBUG, format='%(asctime)s - %(levelname)s - %(message)s')

# Retrieving necessary Azure credentials from environment variables
cluster = os.environ.get('CLUSTER_URL')
database = os.environ.get('DATABASE')
azure_client_id = os.environ.get('AZURE_CLIENT_ID')
azure_client_secret = os.environ.get('AZURE_CLIENT_SECRET')
azure_tenant_id = os.environ.get('AZURE_TENANT_ID')


def run_query(folder_path, connection):
    # List of folders to process in a specific order
    folder_order = [
        "PreProcess",
        "Tables",
        "MaterialisedViews",
        "Functions",
        "UpdatePolicies",
        "RetentionPolicy",
        "ClearStaticTable",
        "InlineIngestions",
        "Mappings",
        "PostProcess"
    ]

    # Constructing the main folder path
    home = os.path.join(os.environ.get('WORKSPACE'), folder_path)
    logging.info(f"Main Folder path: {home}")

    # Processing each folder in the defined order
    for folder_name in folder_order:
        logging.info(f"Started Processing for: {folder_name}")
        read_files_in_directory(os.path.join(home, folder_name), connection)


def read_files_in_directory(directory, connection):
    # Checking if the directory exists
    if not os.path.exists(directory):
        logging.error(f"Directory not found: {directory}")
        return

    try:
        # Iterating through files in the directory
        for file_name in os.listdir(directory):
            file_path = os.path.join(directory, file_name)
            logging.info(f"Attempting to open: {file_path}")
            
            # Processing KQL files
            if os.path.isfile(file_path) and file_name.endswith('.kql'):
                logging.info(f"Started Processing for the file: {file_name}")
                with open(file_path, 'r') as file:
                    connection.execute(database, file.read())
                logging.info(f"Completed Processing file: {file_name}")
    except FileNotFoundError as error:
        logging.error(f"Directory not found: {directory}")
    except KustoServiceError as kusto_error:
        logging.error(f"Kusto Service Error: {kusto_error}")
    except IOError as io_error:
        logging.error(f"IO Error: {io_error}")
    except Exception as e:
        logging.error(f"An unexpected error occurred: {e}")
        raise  # Re-raise the exception for further handling or termination


if __name__ == "__main__":
    # Checking if the script is provided with the required arguments
    if len(sys.argv) != 2:
        print("Usage: python script.py <folder_name>")
        sys.exit(1)

    # Obtaining the folder name from command line argument
    folder_name = sys.argv[1].strip()
    logging.info(f"Received Deployment for Application: {folder_name}")

    # Creating Kusto client instance and running the queries
    kcsb = KustoConnectionStringBuilder.with_aad_application_key_authentication(
        cluster, azure_client_id, azure_client_secret, azure_tenant_id
    )

    with KustoClient(kcsb) as client:
        try:
            run_query(folder_name, client)
        except Exception as e:
            logging.exception(f"Unhandled Exception: {e}")
            sys.exit(1)
