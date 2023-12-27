import os

def execute_query():
    try:
        # Tables
        print("Tables:")
        tables_path = "E:\\var\\apps1\\Tables"
        read_files_in_directory(tables_path)

        # Functions
        print("Functions:")
        functions_path = "E:\\var\\apps1\\Functions"
        read_files_in_directory(functions_path)

        # Views
        print("Views:")
        views_path = "E:\\var\\apps1\\Views"
        read_files_in_directory(views_path)
        
    except FileNotFoundError as error:
        print("File doesn't exist")

def read_files_in_directory(directory):
    try:
        for file_name in os.listdir(directory):
            file_path = os.path.join(directory, file_name)
            print(f"Attempting to open: {file_path}")
            if os.path.isfile(file_path) and file_name.endswith('.txt'):
                with open(file_path, 'r') as file:
                    print(file.read())
    except FileNotFoundError as error:
        print(f"Directory '{directory}' not found")

if __name__ == "__main__":
    execute_query()
