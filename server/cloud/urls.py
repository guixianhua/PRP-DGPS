from django.conf.urls import url
from . import views

urlpatterns = [
  url(r'^$', views.index, name='index'),
  url(r'^list', views.list, name='list'),
  url(r'^get', views.get, name='get'),
  url(r'^upload/(\w+)/(\w+)/(.+)/$', views.upload, name='upload'),
]