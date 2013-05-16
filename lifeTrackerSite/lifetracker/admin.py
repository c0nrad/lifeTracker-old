from django.contrib import admin
from lifetracker.models import Label, Event, UserProfile

class LabelAdmin(admin.ModelAdmin):
    pass
    
class EventAdmin(admin.ModelAdmin):
    pass

class UserProfileAdmin(admin.ModelAdmin):
    pass
    
admin.site.register(Label, LabelAdmin)
admin.site.register(Event, EventAdmin)
admin.site.register(UserProfile, UserProfileAdmin)