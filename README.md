# Use of Pexip Javascript Client API in Android

Pexip provides a flexible, scalable video conference platform that enables interoperability between video conference systems.

This sample could be used to connect with Skype For Business Meetings and Pexip Meetings.

To connect to a Pexip Meeting you need to change below parameters:

                intent.putExtra("node","nodeName"); //node name
                intent.putExtra("conference","conferenceId"); //xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx (this id is in the URL)
                intent.putExtra("userName","userName"); //user name
                intent.putExtra("bw",576); //band width
                intent.putExtra("domain","pexipDomain"); //pexip domain xxxx.xxxx.com
                
                
To connect to a Skype For Business Meeting you need to change below parameters:

                intent.putExtra("node","nodeName"); //node name
                intent.putExtra("conference",getSFBConference("https://meet.lync.com/yourURL", "pexipDomain")); //add your Skype For Business URL here
                intent.putExtra("userName","userName"); //user name
                intent.putExtra("bw",576); //band width
                intent.putExtra("domain","pexipDomain"); //pexip domain xxxx.xxxx.com

References:

https://docs.pexip.com/api_client/api_pexrtc.htm
