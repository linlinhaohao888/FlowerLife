# Generated by Django 2.1.1 on 2018-10-06 04:12

import datetime
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('run', '0001_initial'),
    ]

    operations = [
        migrations.AlterField(
            model_name='runningdata',
            name='beginTime',
            field=models.DateTimeField(default=datetime.datetime(2018, 10, 6, 12, 12, 41, 262839)),
        ),
    ]
