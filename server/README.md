# PRP-DGPS Server
---------------------------------------------------------------------------------
## Requirements:

* Python3 Django

# To Run
---------------------------------------------------------------------------------
## **First Time(Format DB)**
```
python3 manage.py makemigrations
python3 manage.py migrate
```
## **Run Server**
`python3 manage.py runserver ip:port`

# Database Models
---------------------------------------------------------------------------------
## **DataDao**
```
1. id
2. data_type
3. data
4. create_time
```
