import os
from dotenv import load_dotenv
from flask import Flask
import psycopg2
from psycopg2 import OperationalError

# take environment variables from .env.
load_dotenv()

app = Flask(__name__)

# create the database using OPMdatabase.sql
@app.route('/create')
def create_database():
    # Write your database creation logic here
    database_url = os.getenv('DATABASE_URL')
    # Example: execute SQL command to create the database
    conn = psycopg2.connect('postgresql://busybeaver:busybeaver@localhost:1234/postgres')
    cur = conn.cursor()
    file = open('OPMdatabase.sql', 'r')
    sql_script = file.read()
    cur.execute(sql_script)
    conn.commit()
    cur.close()
    conn.close()
    return 'Database created successfully!'  # returns success message


# delete a created database
@app.route('/delete')
def delete_database():
    # Write your database deletion logic here
    database_url = os.getenv('DATABASE_URL')
    # Example: execute SQL command to delete the database
    # Example: return success message
    return 'Database deleted successfully!'  # returns success message


# test route to see if designated URL works
@app.route('/test')
def test_database():
    return 'Its working!'

if __name__ == '__main__':
    app.run(debug=True)
