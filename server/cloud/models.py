from django.db import models
import django.utils.timezone as timezone
# Create your models here.

ACCEPTABLE_FORMATS = ['%d-%m-%Y',       # '25-10-2006'
                      '%d/%m/%Y',       # '25/10/2006'
                      '%d/%m/%y']       # '25/10/06'

class Data(models.Model):
  create_time = models.DateTimeField(db_column='create_time', default=timezone.now)
  data_type = models.CharField(max_length=20, db_column='data_type')
  data = models.CharField(max_length=200, db_column='data')

  # create_time.editable = True

  def __str__(self):
    return self.data