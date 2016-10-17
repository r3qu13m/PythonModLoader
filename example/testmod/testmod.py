from r3qu13m.pml.python import PyMod
from r3qu13m.mc import SidedLogger
from net.minecraft.item import Item
from net.minecraft.creativetab import CreativeTabs

class TestItem(Item):
  def __init__(s):
    Item.__init__(s, 1029 - 256)
    s.setIconIndex(120)
    s.setCreativeTabs(CreativeTabs.tabMisc)

  def hasEffect(s, itemStack):
    return True

  def getItemDisplayName(s, item):
    return "Python Test Item"

class TestMod(PyMod):
  def init(s, event):
    s.testItem = TestItem()
    SidedLogger.log("TestMod has been Initialized.")

  def preinit(s, event):
    pass

  def postinit(s, event):
    pass

instance = TestMod()
