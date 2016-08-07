import Acspy.Clients.SimpleClient
from TYPES import Proposal
from TYPES import Target
from TYPES import TargetList
from TYPES import Position

client = Acspy.Clients.SimpleClient.PySimpleClient()
db = client.getComponent('DataBase')

db.getProposals()

pos1=Position(190.0,87.0)
pos2=Position(280.0,77.0)
pos3=Position(100.0,67.0)
targets = [Target(0, pos1, 10),Target(1, pos2, 10),Target(2, pos3, 10)]
pid = db.storeProposal(targets)
db.getProposals()
db.getProposalStatus(pid)
db.removeProposal(pid)
db.getProposals()
db.getProposalStatus(pid)
pid = db.storeProposal(targets)
db.getProposalStatus(pid)
db.setProposalStatus(pid,1)
db.getProposalStatus(pid)
db.setProposalStatus(pid,2)
db.getProposalStatus(pid)
