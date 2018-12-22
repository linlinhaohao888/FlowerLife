import RPi.GPIO as GPIO
import time
import requests
import json

GPIO.setmode(GPIO.BOARD)
GPIO.setup(8, GPIO.OUT)

while True:
    try:
        res = requests.get('http://47.106.112.133/flowerLife/info/').json()
        print(res)
        if res.get('time') > 0:
            GPIO.output(8, GPIO.HIGH)
            time.sleep(res['time'] * 60)
            GPIO.output(8, GPIO.LOW)
        time.sleep(1)
    except KeyboardInterrupt:
        GPIO.cleanup()
        break
    except:
        GPIO.cleanup()
        GPIO.setmode(GPIO.BOARD)
        GPIO.setup(8, GPIO.OUT)




