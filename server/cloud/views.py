from django.shortcuts import render
from django.shortcuts import HttpResponse

# Create your views here.

def index(request):
 return render(request, 'cloud/index.html', context={
  'title': 'PRP-DGPS',
  'content': 'Welcome PRP-DGPS Server :))'
 })

def upload(request, param1, param2, param3):
    return HttpResponse("data1 = " + param1 + " data2 = " + param2 + " data3 = " + param3)