$ErrorActionPreference = 'Continue'
$log = 'C:\ProgramData\codex_wintun_cleanup.log'

function Write-Log {
    param([string]$Message)
    $line = '{0} {1}' -f (Get-Date -Format 'yyyy-MM-dd HH:mm:ss'), $Message
    Add-Content -LiteralPath $log -Value $line
}

Write-Log '=== cleanup start ==='

try {
    reg delete "HKCU\Software\Microsoft\Windows\CurrentVersion\Run" /v com.oplus.devicespace /f | Out-Null
    Write-Log 'Removed HKCU Run entry com.oplus.devicespace'
} catch {
    Write-Log "Failed removing HKCU Run entry: $($_.Exception.Message)"
}

$svcNames = @('O+Connect Service', 'OplusRemoteService')
foreach ($svc in $svcNames) {
    try {
        Stop-Service -Name $svc -Force -ErrorAction Stop
        Write-Log "Stopped service $svc"
    } catch {
        Write-Log "Stop-Service failed for ${svc}: $($_.Exception.Message)"
    }
    try {
        & sc.exe config "$svc" start= disabled | Out-Null
        Write-Log "Disabled service $svc"
    } catch {
        Write-Log "sc config failed for ${svc}: $($_.Exception.Message)"
    }
}

$procNames = @('O+Connect', 'oplus_remote_ui', 'oplus_remote_service', 'devicespace')
Get-Process -ErrorAction SilentlyContinue |
    Where-Object { $procNames -contains $_.ProcessName } |
    ForEach-Object {
        try {
            Stop-Process -Id $_.Id -Force -ErrorAction Stop
            Write-Log "Stopped process $($_.ProcessName) [$($_.Id)]"
        } catch {
            Write-Log "Stop-Process failed for $($_.ProcessName) [$($_.Id)]: $($_.Exception.Message)"
        }
    }

$wintunPath = 'D:\Software\Oppo Connect\daemon\wintun.dll'
$wintunBak = 'D:\Software\Oppo Connect\daemon\wintun.dll.disabled'
try {
    if (Test-Path -LiteralPath $wintunBak) {
        Remove-Item -LiteralPath $wintunBak -Force
    }
    if (Test-Path -LiteralPath $wintunPath) {
        Rename-Item -LiteralPath $wintunPath -NewName 'wintun.dll.disabled' -Force
        Write-Log 'Renamed O+Connect wintun.dll to wintun.dll.disabled'
    } else {
        Write-Log 'O+Connect wintun.dll not found'
    }
} catch {
    Write-Log "Rename wintun.dll failed: $($_.Exception.Message)"
}

Get-NetAdapter -IncludeHidden |
    Where-Object { $_.InterfaceDescription -like '*Wintun*' -or $_.Name -like 'vgate*' } |
    ForEach-Object {
        try {
            Disable-NetAdapter -Name $_.Name -Confirm:$false -ErrorAction SilentlyContinue | Out-Null
        } catch {}
        try {
            & pnputil.exe /remove-device "$($_.PnPDeviceID)" | Out-Null
            Write-Log "Removed device $($_.PnPDeviceID)"
        } catch {
            Write-Log "remove-device failed for $($_.PnPDeviceID): $($_.Exception.Message)"
        }
    }

Get-PnpDevice |
    Where-Object { $_.InstanceId -like 'SWD\WINTUN\*' -or $_.FriendlyName -like '*Wintun*' } |
    ForEach-Object {
        try {
            & pnputil.exe /remove-device "$($_.InstanceId)" | Out-Null
            Write-Log "Removed stale device $($_.InstanceId)"
        } catch {
            Write-Log "remove-device failed for stale $($_.InstanceId): $($_.Exception.Message)"
        }
    }

try {
    & pnputil.exe /delete-driver oem112.inf /uninstall /force | Out-Null
    Write-Log 'Deleted driver package oem112.inf'
} catch {
    Write-Log "delete-driver failed: $($_.Exception.Message)"
}

Write-Log '=== cleanup end ==='
