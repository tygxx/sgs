import os
import fileinput

# Function to rename files and directories


def rename_files_and_dirs(root_dir, exclude_dirs=None):
    if exclude_dirs is None:
        exclude_dirs = []

    for dirpath, dirnames, filenames in os.walk(root_dir):
        # 排除指定的文件夹
        dirnames[:] = [d for d in dirnames if d not in exclude_dirs]

        print(f"Processing directory: {dirpath}")
        # Rename directories
        for dirname in dirnames:
            if 'ruoyi' in dirname or 'RuoYi' in dirname:
                new_dirname = dirname.replace('ruoyi', 'sgs').replace('RuoYi', 'Sgs')
                os.rename(os.path.join(dirpath, dirname),
                          os.path.join(dirpath, new_dirname))
                print(f"Found directory: {dirname}")

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
                print(line.replace('ruoyi', 'sgs').replace('RuoYi', 'SGS').replace('若依', 'SGS'), end='')


# Specify the root directory to start renaming
# Change this to your target directory
root_directory = '/Users/ty/private_activities/sgs'
exclude_directories = ['ruoyi-ui', 'sgs-ui', 'target', 'logs']  # 添加要排除的文件夹
rename_files_and_dirs(root_directory, exclude_dirs=exclude_directories)
