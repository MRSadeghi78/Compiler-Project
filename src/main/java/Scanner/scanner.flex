
%%

%public
%class Scanner
%standalone
%unicode

EOF = [\r|\n|\r\n]
digit = [0-9]
whiteSpace = " "
other = "="|"!"|"<"|">"|"."|","|":"|";"|"["|"]"|"+"|"-"|"*"|"~"|"&"|"|"|"%"|"^"|"("|")"|"\""|"'"|"{"|"}"|"/"

%{
    String text;
%}

%%

{EOF} {return 1;}
{whiteSpace}+ {text = yytext();return 2;}
\/\/([^{EOF}])* { text = yytext(); return 3;}
"/*" ~ "*/" {text = yytext();return 4;}
{digit}+[L]? {text = yytext(); return 5;}
"0x"({digit}|[ABCDEF])+ {text = yytext();return 6;}
\.[0-9]+[F]? | [0-9]+\.[F]? | [0-9]+\.[0-9]+[F]? {text = yytext();return 7;}
[+|-]?(\.[0-9]+ | [0-9]+\. | [0-9]+\.[0-9]+ | [0-9]+){whiteSpace}*[e]{whiteSpace}*[+-]?{whiteSpace}*{digit}+ {text = yytext(); return 8;}
\'[^\']\' | \'\\[^\']\' {text = yytext(); return 9;}
\" (\\\" | [^\"])+ \" {text = yytext(); return 10;}
[a-z]+ {text = yytext();return 11;}
[a-zA-Z]([_]?[a-zA-Z0-9]+)* {text = yytext(); return 12;}
{other} {text = yytext(); return 13;}
