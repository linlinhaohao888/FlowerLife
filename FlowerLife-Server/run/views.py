from django.shortcuts import render
from django.http import HttpResponse
from django.contrib.auth import get_user_model, authenticate
from .models import RunningData
from datetime import datetime

User = get_user_model()


# Create your views here.
def update(request):
    if request.method == 'POST':
        username = 'username'
        password = 'password'
        user = auth(username, password)
        distance = request.POST.get('distance')
        time = request.POST.get('time')
        begin_time = int(request.POST.get('beginTime'))
        data = RunningData(distance=distance, time=time, begin_time=datetime.fromtimestamp(begin_time), user=user)
        data.save()
        return HttpResponse('OK')
    return HttpResponse('Fail')


def auth(username, password):
    if username is not None and password is not None:
        user = authenticate(username=username, password=password)
        if user is not None:
            return user
    return None
