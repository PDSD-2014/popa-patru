# Words Cocktail Server
import socket
import sys
import json
import time
try:    
    import thread 
except ImportError:
    import _thread as thread

HOST = "192.168.173.1"
PORT = 28028
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print("Socket created")
try:
    s.bind((HOST, PORT))
except (socket.error, msg):
    print ("Bind failed. Error code: " + str(msg[0]) + "Error message: " + msg[1])
    sys.exit()
print ("Socket bind complete")
s.listen(10)
print ("Socket now listening")

# number of connected players
playerNo = 0
# global board
board = ""
allScore = ""
gameStarted = 0

#Function for handling connections. This will be used to create threads
def clientthread(name, conn):
	global playerNo
	global board
	global allScore
	global gameStarted
	
	# send complete name
	try:
		conn.send(name + "\n")
		print "Sent complete name " + name
	except:
		print "connection closed when trying to send name"
	
	# increment player
	playerNo = playerNo + 1
	
	# receive board
	try:
		b = conn.recv(1024)
		print "Received board " + b
		if (board == ""):
			board = b
			print "Board saved"
		else:
			print "Another board will be used: " + board
	except:
		print "Error on board receive"
		
	# send playerNo
	try:
		conn.send(str(playerNo) + "\n")
		print "Send playerNo"
	except:
		print "connection closed when trying to send playno"
	allScore = ""
	if (playerNo < 2):
		while (playerNo < 2):
			continue	
		try:
			conn.send(str(playerNo) + "\n")
			print "Game can now begin"
		except:
			print "connection closed when trying to send playno"
		
	# send board
	try:
		conn.send(board)
		print "Board sent! " + board
	except:
		print "connection closed when trying to send board"
	
	# receive score
	score = "0"
	try:
		score = conn.recv(1024)
		print "Received score " + score
	except:
		print "Error receiving score"
		
	allScore = allScore + ";" + name + " has " + score
	time.sleep(5)
	
	# send score
	try:
		conn.send(allScore.replace("\n", "") + "\n")
		print "score sent! " + allScore
	except:
		print "connection closed when trying to send score"
	
	playerNo = 0
	board = ""
	
	print "closed"
	conn.close()

#now keep talking with the client
while True:
    #wait to accept a connection - blocking call
	conn, addr = s.accept()
	print ("Connected with " + addr[0] + ":" + str(addr[1]))
	name = "Anon"
	try:
			name = conn.recv(1024)
			print("Player " + name + " is now connected")
	except:
			print "connection closed when trying to receive Name"
			
	#start new thread takes 1st argument as a function name to be run, second is the tuple of arguments to the function.
	thread.start_new_thread(clientthread ,(name.replace("\n", "") + str(addr[1]), conn,))
	
s.close()
