from django.db import models
from django.contrib.auth.models import User

# Create your models here.
class Event(models.Model):
    label = models.CharField(max_length=50)
    startdatetime = models.DateTimeField()
    enddatetime = models.DateTimeField(blank=True, null=True)
    description = models.CharField(max_length=128, blank=True)

    def __unicode__(self):
        return self.label
 
class UserProfile(models.Model):
    user = models.ForeignKey(User, unique=True)
    events = models.ManyToManyField('Event', blank=True)

User.profile = property(lambda u: UserProfile.objects.get_or_create(user=u)[0])
