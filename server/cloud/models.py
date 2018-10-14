from django.db import models

# Create your models here.

class DataDao(models.Model):
  id = models.IntegerField(auto_now_add=True)
  create_time = models.TimeField()
  data_type = models.CharField()
  data = models.CharField()

  def __str__(self):
    return self.title