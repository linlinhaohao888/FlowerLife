import RPi.GPIO as GPIO
import time
import requests
import json

GPIO.setmode(GPIO.BOARD)
GPIO.setup(8, GPIO.OUT)

while True:
    try:
        res = requests.get('http://183.172.209.247:8000/flowerLife/info/').json()
        GPIO.output(8, GPIO.HIGH)
        time.sleep(res['time'] * 60)
        GPIO.output(8, GPIO.LOW)
        time.sleep(1)
    except:
        GPIO.cleanup()
        GPIO.setmode(GPIO.BOARD)
        GPIO.setup(8, GPIO.OUT)

