from django.db import models

# Create your models here.

class DataDao(models.Model):
  create_time = models.TimeField(db_column='create_time')
  data_type = models.CharField(max_length=20, db_column='data_type')
  data = models.CharField(max_length=200, db_column='data')

  def __str__(self):
    return self.data