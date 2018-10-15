from django.shortcuts import render
from django.shortcuts import HttpResponse

from cloud.models import Data
# Create your views here.

def index(request):
 return render(request, 'cloud/index.html', context={
  'title': 'PRP-DGPS',
  'content': 'Welcome PRP-DGPS Server :))'
 })

# http://Active_Server_IP:Active_Server_Port/upload/data_type/data/time/
def upload(request, param1, param2, param3):
  data = Data(data_type = param1, data = param2)
  data.save()
  return HttpResponse("Upload data_type = " + param1 + " data = " + param2 + " create = " + param3)

def list(request):
  data_list = Data.objects.all()
  data_list2 = []
  for data in data_list:
    data.create_time = data.create_time.strftime('%Y-%m-%d %H:%I:%S')
    data_list2.append(data)
    print ('dataType: ' + data.data_type + ' data: ' + data.data + ' time: ' + data.create_time)

  for data in data_list2:
    print ('dataType: ' + data.data_type + ' data: ' + data.data + ' time: ' + data.create_time)
  # print (data_list)
  return render(request, 'cloud/list.html', context={
    'title': 'PRP-DGPS Server',
    'data_list': data_list2
  })