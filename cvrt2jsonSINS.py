import json;	
dic_list = dict();
lines = tuple(open("iSlangSins.txt", 'r'))
dic_json = open('wordSins.json', 'w')
for line in lines :
	meaning = line.split(" " , 1)[1];
	word = line.split(" " , 1)[0];
	dic_list[word] = meaning[:len(meaning)-1];
dic_list = json.dumps(dic_list);
dic_json.write(dic_list)
