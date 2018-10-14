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
```
python3 manage.py runserver Active_Server_IP:Active_Server_Port
```
Once you start the server, you can open its web UI by
```
http://Active_Server_IP:Active_Server_Port
```

# To Use
---------------------------------------------------------------------------------
## **Upload Data**
```
http://Active_Server_IP:Active_Server_Port/upload/data_type/data/time/
```

# Database Models
---------------------------------------------------------------------------------
## **DataDao**
```
1. id
2. data_type
3. data
4. create_time
```
