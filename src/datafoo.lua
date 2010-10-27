-- Sample Plugin

require "luasql.postgres";

usage = {};
usage["DBCONNECT"] = "DBCONNECT <IP> <PORT>";
usage["DBCONNECT1"] = "DBCONNECT <IP> <PORT>";
usage["DBCONNECT2"] = "DBCONNECT <IP> <PORT>";
usage["DBCONNECT3"] = "DBCONNECT <IP> <PORT>";
usage["DBCONNECT4"] = "DBCONNECT <IP> <PORT>";

hooks = {};
hooks["INCOMING"] = false;
hooks["OUTGOING"] = false;

function process( cmd )
command = string.match(cmd, "%w+");

if( string.upper(command) == "DBCONNECT") then

return dbconnect(cmd);
else
return "Command Failed";
end

end

-----------------------------------------------------------------

function dbconnect(cmd)

print("Herro from dbconnect");
env = luasql.postgres();

if env == nil then
print("Failed")
else
print("Connected")
end

db = env:connect("dbname=luatest user=postgres password=furbias411 hostaddr=127.0.0.1")

if db == nil then
print("db Failed")
else
print("db Connected")
end

cur = db:execute("select * from foodata");

if cur == nil then
print("cur Failed")
else
print("cur got something")
end

data = {};
cur:fetch(data, "n");

if(data == nil) then
print(">_>");
else
print("k00, got some data");
end

print(data[1], data[2]);

while cur:fetch(data, "n") do
print(data[1], data[2]);
end

db:close();

return "RAN DB CODE";

end
