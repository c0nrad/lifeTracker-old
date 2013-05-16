import datetime as dt
from django import forms
from django.core import validators
from django.contrib.auth.models import User
from lifetracker.models import  Event

class RegistrationForm(forms.Form):
    username = forms.CharField(required=True)
    email = forms.EmailField(required=True)
    password = forms.CharField(widget=forms.PasswordInput())
    password1 = forms.CharField(widget=forms.PasswordInput())
    
    def isValidUsername(self, field_data, all_data):
        try:
            User.objects.get(username=field_data)
        except User.DoesNotExist:
            return
        raise validators.ValidationError('The username "%s" is already taken.' % field_data)
    
class LoginForm(forms.Form):
    username = forms.CharField(required=True)
    password = forms.CharField(widget=forms.PasswordInput())

class AddEventForm(forms.Form):
    input_formats = ['%Y-%m-%d %H:%M:%S',    # '2006-10-25 14:30:59'
     '%Y-%m-%d %H:%M',        # '2006-10-25 14:30'
     '%Y-%m-%d',              # '2006-10-25'
     '%m/%d/%Y %H:%M:%S',     # '10/25/2006 14:30:59'
     '%m/%d/%Y %H:%M',        # '10/25/2006 14:30'
     '%m/%d/%Y',              # '10/25/2006'
     '%m/%d/%y %I:%M:%S',     # '10/25/06 14:30:59'
     '%m/%d/%y %H:%M',        # '10/25/06 14:30'
     '%m/%d/%y',              # '10/25/06'
     '%m-%d-%Y %I:%M:%S %p']   #  05-11-2013 12:59:27 PM

    label = forms.CharField(required=True)
    startdatetime = forms.DateTimeField(required=True, initial=dt.datetime.now(), input_formats = input_formats)
    enddatetime = forms.DateTimeField(required=False, initial=dt.datetime.now(), input_formats = input_formats)
    description = forms.CharField(required=False, widget=forms.Textarea(attrs={'rows':4, 'cols':50}))

    def __init__(self, *args, **kwargs):
        request = kwargs.pop('request', None)
        super(AddEventForm, self).__init__(*args, **kwargs)

        if request:
            print request.user.profile.events.all()
            self.fields['link'] = forms.ModelChoiceField(queryset=request.user.profile.events.all(), required=False)

    

class ForgotAccountForm(forms.Form):
    email = forms.EmailField(required=True)
