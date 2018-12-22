from django.shortcuts import render
from django.http import HttpResponse
from django.contrib.auth import get_user_model, authenticate
from .models import RunningData
from datetime import datetime
import json

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


def info(request):
    if request.method == 'GET':
        username = 'username'
        password = 'password'
        user = auth(username=username, password=password)
        now = datetime.now()
        data = RunningData.objects.filter(user=user, begin_time__year=now.year, begin_time__month=now.month,
                                          begin_time__day=now.day)
        response = {'time': len(data)}
        return HttpResponse(json.dumps(response), content_type='application/json')


def auth(username, password):
    if username is not None and password is not None:
        user = authenticate(username=username, password=password)
        if user is not None:
            return user
    return None
