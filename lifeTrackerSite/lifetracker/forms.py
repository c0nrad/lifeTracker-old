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
    username = forms.CharField(required=True, initial="username")
    password = forms.CharField(widget=forms.PasswordInput(), initial="password")    

class EventForm(forms.ModelForm):
    input_formats = ['%Y-%m-%d %H:%M:%S',    # '2006-10-25 14:30:59'
     '%Y-%m-%d %H:%M',        # '2006-10-25 14:30'
     '%m/%d/%Y %H:%M:%S',     # '10/25/2006 14:30:59'
     '%m/%d/%Y %H:%M',        # '10/25/2006 14:30'
     '%m/%d/%y %I:%M:%S',     # '10/25/06 14:30:59'
     '%m/%d/%y %H:%M',        # '10/25/06 14:30'
     '%b %d, %Y, %I:%M %p'    #  May 10, 2013, 1:13 pm (Javascript mmm d, yyyy, h:MM tt)
     '%m-%d-%Y %I:%M:%S %p']  #  05-11-2013 12:59:27 PM


    label = forms.CharField(required=True)
    startdatetime = forms.DateTimeField(required=True, input_formats=input_formats, widget=forms.DateTimeInput(attrs={'data-format':'MM-dd-yyyy HH:mm:ss PP'}))
    enddatetime = forms.DateTimeField(required=False, input_formats=input_formats, widget=forms.DateTimeInput(attrs={'data-format':'MM-dd-yyyy HH:mm:ss PP'}))
    description = forms.CharField(required=False, widget=forms.Textarea(attrs={'rows': '4', 'cols': '50'}))

    class Meta:
        model = Event

class ForgotAccountForm(forms.Form):
    email = forms.EmailField(required=True)


class ContactForm(forms.Form):
    name = forms.CharField(required=False);
    email = forms.EmailField(required=False);
    message = forms.CharField(required=True);    