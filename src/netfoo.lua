require('socket')

usage = {};
usage["NETCONNECT"] = "NETCONNECT <IP> <PORT>";
usage["NETCONNECT1"] = "NETCONNECT <IP> <PORT>";
usage["NETCONNECT2"] = "NETCONNECT <IP> <PORT>";
usage["NETCONNECT3"] = "NETCONNECT <IP> <PORT>";
usage["NETCONNECT4"] = "NETCONNECT <IP> <PORT>";

hooks = {};
hooks["INCOMING"] = true;
hooks["OUTGOING"] = false;

function process( cmd )
command = string.match(cmd, "%w+");

if( string.upper(command) == "NETCONNECT") then

return netconnect(cmd);
else
return "Command Failed";
end

end

-----------------------------------------------------------------

client = socket.tcp();

function netconnect( cmd )
print("In NetConnect");
client:connect("127.0.0.1", "3333");
end


function incoming( data )
if client:send(data) then
return "NET TEST WORKED";
else
return "NET TEST FAILED";
end
end

function outgoing( data )
if client:send(data) then
return "NET TEST WORKED";
else
return "NET TEST FAILED";
end
end
