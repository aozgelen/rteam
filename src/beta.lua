-- test

usage = {};
usage["DBCONNECT"] = "DBCONNECT <IP> <PORT>";
usage["DBCONNECT1"] = "DBCONNECT <IP> <PORT>";
usage["DBCONNECT2"] = "DBCONNECT <IP> <PORT>";
usage["DBCONNECT3"] = "DBCONNECT <IP> <PORT>";
usage["DBCONNECT4"] = "DBCONNECT <IP> <PORT>";

function process( cmd )
command = string.match(cmd, "%w+");

if( string.upper(command) == "DBCONNECT") then
return "Command Succeeded";
else
return "Command Failed";
end

end
