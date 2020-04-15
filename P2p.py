import nfc
import threading
import ndef
import nfc.snep
import sys

def on_connect(llc):
    threading.Thread(target=llc.run).start()
    return False

clf = nfc.ContactlessFrontend('usb')
imageData = sys.stdin.read()

llc = clf.connect(llcp={'on-connect': on_connect})
snep = nfc.snep.SnepClient(llc)

passed = snep.put_octets(imageData)

if passed:
    print "true"

else:
    print "false"
