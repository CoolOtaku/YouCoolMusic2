import youtube_dl
import json
import urllib

def main(id) :
	ydl = youtube_dl.YoutubeDL({'outtmpl': '%(id)s.%(ext)s'})
	f = { 'v' : id}
	url = 'https://www.youtube.com/watch?'+urllib.parse.urlencode(f)

	with ydl:
		result = ydl.extract_info(url, download=False)

	sound = result['formats'][0]['url']
	video = result['requested_formats'][0]['url']
	res = json.dumps({'audio': sound, 'video': video}, sort_keys=True)
	return(res)