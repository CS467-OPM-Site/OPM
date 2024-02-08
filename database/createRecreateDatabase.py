"""
Script creates AND recreates the OPM database, from scratch.

Any data contained within the database WILL be deleted.
"""
import os
from dotenv import load_dotenv
import psycopg2
import argparse

# take environment variables from .env.
load_dotenv()
# Example URL to connect to: postgresql://ryubarrett:password@localhost:5432/opmdatabase
database_url = os.getenv('DATABASE_URL')

# File should be in same directory as python script
CREATION_SQL_FILE = "database\RecreateOPMDatabase.sql"
MOCKDATA_SQL_FILE = "database\MockDataOPMDatabase.sql"


# create the database using OPMdatabase.sql
def create_database():
    run_script(CREATION_SQL_FILE)
    print('Database created successfully!')


# add mock data into the created database
def mock_data():
    run_script(MOCKDATA_SQL_FILE)
    print('Mock data successfully added!')


# helper function that opens and runs given SQL script
def run_script(script):
    conn = psycopg2.connect(database_url)
    cur = conn.cursor()

    file = open(script, 'r')
    sql_script = file.read()
    cur.execute(sql_script)

    conn.commit()
    cur.close()
    conn.close()


if __name__ == '__main__':
    option = input("press 1 to create database\npress 2 to create mock data\npress 3 to create database and mock data\n")
    if option == "1":
        create_database()
    if option == "2":
        mock_data()
    if option == "3":
        create_database()
        mock_data()
