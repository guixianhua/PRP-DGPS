# Generated by Django 2.1 on 2018-10-14 12:35

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='DataDao',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('create_time', models.TimeField(db_column='create_time')),
                ('data_type', models.CharField(db_column='data_type', max_length=20)),
                ('data', models.CharField(db_column='data', max_length=200)),
            ],
        ),
    ]
