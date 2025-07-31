@echo off
cd /d "C:\Users\Thirvo\IdeaProjects\todoapp"
echo Starting Todo Web Application...
java -Djava.awt.headless=true -cp "target/classes;target/lib/*" com.example.todoapp.web.TodoWebOnlyApplication
pause

