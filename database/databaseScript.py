import os
from dotenv import load_dotenv
import psycopg2

# take environment variables from .env.
load_dotenv()


# create the database using OPMdatabase.sql
def create_database():
    # Write your database creation logic here
    database_url = os.getenv('DATABASE_URL')
    # Example: execute SQL command to create the database
    conn = psycopg2.connect('postgresql://ryubarrett:password@localhost:5432/opmdatabase')
    cur = conn.cursor()
    file = open('OPMdatabase.sql', 'r')
    sql_script = file.read()
    cur.execute(sql_script)
    conn.commit()
    cur.close()
    conn.close()
    print('Database created successfully!')  # returns success message

if __name__ == '__main__':
    create_database()
