function Verify-MasterAndClean {
  # Check if we're on the master branch
  $currentBranch = git rev-parse --abbrev-ref HEAD
  if ($currentBranch -ne "master") {
    Write-Host "Error: Not on the 'master' branch. Please switch to 'master' before proceeding."
    exit 1  # Indicate failure
  }

  $statusOutput = git status --porcelain
  if ($statusOutput) { 
    Write-Host "Unsaved changes detected:"
    Write-Host $statusOutput 
    exit -1
  }
  else { 
    Write-Host "Verification successful: On 'master' branch with no unsaved changes."
  }
}

function Increment-VersionCode {
  $filePath = "./app/build.gradle.kts"
  $tempFilePath = "temp.txt"

  Get-Content $filePath | ForEach-Object {
    if ($_ -match "versionCode =") {
      $_ = $_ -replace "(\d+)", { param($m) [int]$m.Value + 1 }
    }
    $_
  } | Set-Content $tempFilePath

  Move-Item -Path $tempFilePath -Destination $filePath -Force
}

function Build-Bundle {
  $projectPath = "$HOME/StudioProjects/venom-android/"

  # Build the app bundle
  & .\gradlew bundleRelease

  # Path to your keystore
  $keystorePath = "/Users/mitchstark/venomkeystore"

  # Key alias
  $keyAlias = "venomkeystore"

  # Path to the unsigned bundle
  $unsignedBundle = "$projectPath/app/build/outputs/bundle/release/app-release.aab"

  # Sign the app bundle
  & jarsigner -verbose -keystore $keystorePath -storepass $env:KEY_PASSWORD -keypass $env:KEY_PASSWORD $unsignedBundle $keyAlias
}

if (-not $env:KEY_PASSWORD) {
  Write-Host "KEY_PASSWORD is not set"
  exit 1
}

# Main script logic
Set-Location -Path "$HOME/StudioProjects/venom-android/" -ErrorAction Stop

Verify-MasterAndClean

# Increment version code
Increment-VersionCode

# Build the app bundle
Build-Bundle

# Commit the changes
git add .
git commit -m "automated release commit"
git push

# Open the directory containing the build outputs
Start-Process -FilePath "explorer.exe" -ArgumentList "./app/build/outputs/bundle/release/"
