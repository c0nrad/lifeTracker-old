from django.db import models
from django.contrib.auth.models import User

# Create your models here.
class Label(models.Model):
    value = models.CharField(max_length=50)

    def __unicode__(self):
        return self.value
        
class Event(models.Model):
    name = models.CharField(max_length=50)
    label = models.ForeignKey('Label')
    datetime = models.DateTimeField('datetime event')
    links = models.ManyToManyField('Event', blank=True)
    description = models.CharField(max_length=128)

    def __unicode__(self):
        return self.name
 
class UserProfile(models.Model):
    user = models.ForeignKey(User, unique=True)
    events = models.ManyToManyField('Event', blank=True)
    labels = models.ManyToManyField('Label', blank=True)


User.profile = property(lambda u: UserProfile.objects.get_or_create(user=u)[0])
