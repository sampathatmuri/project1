from collections import defaultdict

# Given list of paths
file_paths = [
    "/var/Lizard/Policies/LizardPolicies.kql",
    "/var/Park/Tables/ParkTable.kql",
    "/var/Park/Functions/ParkFunctions.kql",
   
    "/var/Garden/Functions/GardenFunctions.kql",
    "/var/Lizard/Tables/LizardTables.kql",
    "/var/Lizard/Functions/LizardFunctions.kql"
    # ... more paths
]

# Define the order of subfolders within each main folder
folder_order = [
    "Tables",
    "Functions",
    "mappings",
    "update Polciies",
    "Materialized views",
    "Clear static table",
    "inline ingestions"
    # ... add more if needed
]

# Initialize a dictionary to store paths by folder
sorted_paths = defaultdict(list)

# Sort paths into folders
for path in file_paths:
  
    # Spliting the path to array, so that we can get folder and subfolder name
    parts = path.split('/')
   
    folder = parts[2]  # Get the folder name (Lizard, Park, Garden)
    subfolder = parts[3]  # Get the subfolder name (Tables, Functions, etc.)
    print(folder)
    print(subfolder)
    sorted_paths[folder].append((subfolder, path))

# Sort paths within each folder based on the specified order
for folder, paths in sorted_paths.items():
    sorted_paths[folder] = [p for folder_order_item in folder_order for p in paths if p[0] == folder_order_item]

# Print the sorted paths
for folder, paths in sorted_paths.items():
    print(f"Folder: {folder}")
    for _, path in paths:
        print(path)
