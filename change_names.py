import os
import fileinput

# Function to rename files and directories


def rename_files_and_dirs(root_dir):
    for dirpath, dirnames, filenames in os.walk(root_dir):
        # Rename directories
        for dirname in dirnames:
            if 'ruoyi' in dirname:
                new_dirname = dirname.replace('ruoyi', 'sgs')
                os.rename(os.path.join(dirpath, dirname),
                          os.path.join(dirpath, new_dirname))

        # Rename files and modify file contents
        for filename in filenames:
            # Rename files starting with 'RuoYi'
            if filename.startswith('RuoYi'):
                new_filename = 'Sgs' + filename[5:]  # Replace 'RuoYi' with 'Sgs'
                os.rename(os.path.join(dirpath, filename),
                          os.path.join(dirpath, new_filename))
                filename = new_filename  # Update filename for content replacement

            # Rename files containing 'ruoyi'
            elif 'ruoyi' in filename:
                new_filename = filename.replace('ruoyi', 'sgs')
                os.rename(os.path.join(dirpath, filename),
                          os.path.join(dirpath, new_filename))
                filename = new_filename  # Update filename for content replacement

            # Modify file contents
            file_path = os.path.join(dirpath, filename)
            for line in fileinput.input(file_path, inplace=True, encoding='utf-8', errors='ignore'):
                print(line.replace('ruoyi', 'sgs').replace(
                    'RuoYi', 'Sgs'), end='')


# Specify the root directory to start renaming
# Change this to your target directory
root_directory = '/Users/ty/private_activities/sgs'
rename_files_and_dirs(root_directory)
