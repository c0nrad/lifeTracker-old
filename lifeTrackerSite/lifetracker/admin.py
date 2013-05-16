from django.contrib import admin
from lifetracker.models import Event, UserProfile

class EventAdmin(admin.ModelAdmin):
    pass

class UserProfileAdmin(admin.ModelAdmin):
    pass
    
admin.site.register(Event, EventAdmin)
admin.site.register(UserProfile, UserProfileAdmin)