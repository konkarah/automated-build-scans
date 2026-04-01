- name: Run database migrations before deployment
  hosts: app_servers[0]  # Run on first server only
  tasks:
    - name: Run migrations
      docker_container:
        name: migration-runner
        image: "{{ image_name }}:{{ image_tag }}"
        command: "java -jar app.jar --migrate"
        state: started
        detach: no
      run_once: true

- name: Deploy application
  hosts: app_servers
  tasks:
    - name: Start application
      docker_container:
        name: secure-app
        image: "{{ image_name }}:{{ image_tag }}"
        state: started
```

---

## 📊 **Summary: The Complete Picture**

### **What Each Component Does:**

| Component | Role | Why Needed |
|-----------|------|------------|
| **Your Mac** | Development | Write code |
| **GitHub** | Source control | Store code, trigger builds |
| **Jenkins** | Automation | Build, test, scan, orchestrate |
| **Maven** | Build tool | Compile Java, manage dependencies |
| **Docker** | Containerization | Package app with dependencies |
| **Trivy** | Security scanner | Find vulnerabilities |
| **Docker Registry** | Image storage | Store approved images |
| **Ansible** | Deployment | Push to production servers |
| **Production Servers** | Runtime | Serve users |

### **Why Install on Jenkins?**

✅ **Isolated environment** - Jenkins can't use your Mac's tools  
✅ **Consistent builds** - Everyone uses same versions  
✅ **Automated** - No manual steps  
✅ **Repeatable** - Same process every time  
✅ **Auditable** - Complete logs of every build  

### **The Security Gate (Most Important Part):**
```
Without Trivy:
Developer → Build → Deploy → Production → 💥 HACKED

With Trivy:
Developer → Build → Scan → ❌ REJECTED → Fix vulnerabilities
                         → ✅ APPROVED → Deploy → Production → 🛡️ SECURE