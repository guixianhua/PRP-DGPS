from django.conf.urls import url
from . import views

urlpatterns = [
  url(r'^$', views.index, name='index'),
  url(r'^upload/p1(\w+)p2(\w+)p3(.+)/$', views.upload, name='upload'),
]