On Error Resume Next
  Dim fso, folder, files, NewsFile, sFolder, delimeter
  
  delimeter = ";"
 
  Set fso = CreateObject("Scripting.FileSystemObject")
  sFolder = Wscript.Arguments.Item(0)
  If sFolder = "" Then
      Wscript.Echo "No Folder parameter was passed"
      Wscript.Quit
  End If
  delim = Wscript.Arguments.Item(1)
  If delim <> "" Then
    delimeter = delim
  End if
  
  Set folder = fso.GetFolder(sFolder)
  Set files = folder.Files
  
  paths = ""
  Set regEx = New RegExp
  regEx.Pattern = ".jar$"

  For each folderIdx In files
    If regEx.Test(folderIdx.Name) then
      If paths <> "" then
        paths = paths & delimeter
      End if
      paths = paths & sFolder & "/" & folderIdx.Name
    End if
  Next

  wscript.echo paths

