# Get the location of the current folder
Set-Location $PSScriptRoot

# Compile all the scala files in one directory
scalac *.scala

# Run the experiment and collect the results in txt format
scala $PSScriptRoot\game.scala

# Get all the text files outputed
$txtFiles = Get-ChildItem -Path $PSScriptRoot -Filter *.txt

#copy each of them to the location of the python script
foreach ($file in $txtFiles){
    Copy-Item -Path $file.FullName -Destination $PSScriptRoot\..\..\ -Force
}

# Run the python file in the outer folder
Set-Location $PSScriptRoot\..\..\
python gametheory.py