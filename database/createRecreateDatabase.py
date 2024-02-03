"""
Script creates AND recreates the OPM database, from scratch.

Any data contained within the database WILL be deleted.
"""
import os
from dotenv import load_dotenv
import psycopg2

# take environment variables from .env.
load_dotenv()

# File should be in same directory as python script
CREATION_SQL_FILE = "RecreateOPMDatabase.sql"


# create the database using OPMdatabase.sql
def create_database():
    # Write your database creation logic here
    database_url = os.getenv('DATABASE_URL')

    # Example URL to connect to: postgresql://ryubarrett:password@localhost:5432/opmdatabase
    conn = psycopg2.connect(database_url)
    cur = conn.cursor()

    file = open(CREATION_SQL_FILE, 'r')
    sql_script = file.read()
    cur.execute(sql_script)

    conn.commit()
    cur.close()
    conn.close()

    print('Database created successfully!')  

if __name__ == '__main__':
    create_database()
