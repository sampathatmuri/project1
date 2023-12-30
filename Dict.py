import sys
import os
import logging
from azure.kusto.data import KustoClient, KustoConnectionStringBuilder
from azure.kusto.data.exceptions import KustoServiceError

# Set up logging
logging.basicConfig(filename='kusto_script.log', level=logging.DEBUG, format='%(asctime)s - %(levelname)s - %(message)s')

cluster = os.environ.get('CLUSTER_URL')
database = os.environ.get('DATABASE')
azure_client_id = os.environ.get('AZURE_CLIENT_ID')
azure_client_secret = os.environ.get('AZURE_CLIENT_SECRET')
azure_tenant_id = os.environ.get('AZURE_TENANT_ID')


def run_query(folder_path, connection):
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
    home = os.path.join(os.environ.get('WORKSPACE'), folder_path)
    logging.info(f"Main Folder path: {home}")
    for folder_name in folder_order:
        logging.info(f"Started Processing for: {folder_name}")
        read_files_in_directory(os.path.join(home, folder_name), connection)


def read_files_in_directory(directory, connection):
    if not os.path.exists(directory):
        logging.error(f"Directory not found: {directory}")
        return

    try:
        for file_name in os.listdir(directory):
            file_path = os.path.join(directory, file_name)
            logging.info(f"Attempting to open: {file_path}")
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
    if len(sys.argv) != 2:
        print("Usage: python script.py <folder_name>")
        sys.exit(1)

    folder_name = sys.argv[1].strip()
    logging.info(f"Received Deployment for Application: {folder_name}")

    kcsb = KustoConnectionStringBuilder.with_aad_application_key_authentication(
        cluster, azure_client_id, azure_client_secret, azure_tenant_id
    )

    with KustoClient(kcsb) as client:
        try:
            run_query(folder_name, client)
        except Exception as e:
            logging.exception(f"Unhandled Exception: {e}")
            sys.exit(1)
