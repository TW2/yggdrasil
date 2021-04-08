How to use YggLock
-
To communicate between two (or more) Yggdrasil software (an IRC-like with emojis) YggLock has an object called CryptObj which contains your surname, your ip, your port and a password.

This CryptObj key that is generated with YggLock command line is a key to put in a ```*.txt``` file.

For example, you can put in ```Sylvain.txt``` the generated key:
```
java -jar ygglock-1.0.jar
Crypt, put your surname:
Sylvain
Crypt, put your ip:
123.45.67.89
Crypt, put your port:
12345
Crypt, put your 8 characters password:
12345678
Your key is:
uJKl6h6Bj6ktel/Z0MgHjBU7k/Vkh+I3VVpXO0+SER4m3YX90plgoJZsXCCze8Ld3dXEtAocaRg2N1Jb805xKHJogZmMI5fK9y2j1S3rKpr82rRc4Smzy0VQ4RiZkr2naA8nEAtMWV8QAAX7uBBJFofSxACLzzQS5cOYo6K9/Js=
```

This tool can encrypt and decrypt! In Yggdrasil and and with this tool you can only decrypt your surname, ip and port. Password is set but the decrypt method cannot give the password unless you have the 'hello' which is not a valid password.
