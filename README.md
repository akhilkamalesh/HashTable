# HashTable
Creation of HashTable using GIS (Geographic Information System) Records

GISRecord is an object that takes in a string gisRecord and long offset (where the record is in the text file). From there, it splits the gisRecord and
obtains important infomration such as featureName, featureClass, latitude, longitude, etc.

Hashable is an interface that is implemented in NameEntry

NameEntry has two fields which is a key and ArrayList<long> Locations of the files offsets of the matching records
  
HashTable is the object where the records (keys) can be put into a index.
  
nameIndexer holds the main class and runs the program; It parses through a command file (a text file that has line that tells you what commands to read through and do) and based on this, methods in this class are called to do those commands such as what_is, show, index (creates the hash table)

