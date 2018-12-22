from django.db import models
from datetime import datetime
from django.contrib.auth.models import AbstractUser


# Create your models here.
class User(AbstractUser):
    def __str__(self):
        return self.username


class RunningData(models.Model):
    distance = models.FloatField(default=0)
    time = models.IntegerField(default=0)
    begin_time = models.DateTimeField(default=datetime.now())
    user = models.ForeignKey(User, on_delete=models.CASCADE, default='')

    def __str__(self):
        return datetime.strftime(self.begin_time, '%Y-%m-%d %H:%M:%S')
