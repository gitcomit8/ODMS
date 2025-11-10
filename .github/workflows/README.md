# GitHub Actions Workflow

## Build and Push GraalVM Native Image

This workflow automatically builds a GraalVM native image and pushes it to the `odms-artifacts` repository whenever code is pushed to the `main` branch.

### What It Does

1. **Triggers on:**
   - Push to `main` branch
   - Manual trigger via `workflow_dispatch`

2. **Build Process:**
   - Checks out source code
   - Captures the commit message from the main branch
   - Builds GraalVM native image using `Dockerfile.native`
   - Exports the Docker image as a compressed tar file
   - Pushes to `gitcomit8/odms-artifacts` repository
   - Uses the **same commit message** from the source commit

3. **Output:**
   - `odms-native-image.tar.gz` - Compressed Docker image
   - `native-image-info.txt` - Build metadata

### Requirements

- **GitHub Secret:** `PAT` (Personal Access Token)
  - Must have write access to `gitcomit8/odms-artifacts`
  - Set in repository Settings → Secrets → Actions

### Build Time

- **Expected Duration:** 5-10 minutes
- Native compilation is memory-intensive
- GitHub Actions runners have sufficient resources

### Using the Built Image

After the workflow completes, download from `odms-artifacts`:

```bash
# Download from artifacts repo
curl -L https://github.com/gitcomit8/odms-artifacts/raw/main/odms-native-image.tar.gz -o odms-native-image.tar.gz

# Extract and load
gunzip odms-native-image.tar.gz
docker load -i odms-native-image.tar

# Run the image
docker run -d -p 80:80 odms-native:latest
```

### Performance

- **Startup Time:** 2-5 seconds
- **Memory Usage:** 80-150 MB
- **Image Size:** ~150-200 MB compressed

### Manual Trigger

You can manually trigger the build from GitHub:
1. Go to Actions tab
2. Select "Build and Push GraalVM Native Image to Artifacts Repo"
3. Click "Run workflow"
4. Select branch and run

### Troubleshooting

**Build Fails with "Out of Space":**
- The workflow includes disk cleanup steps
- GitHub Actions runners have ~14GB free space
- Native builds need ~8GB

**Build Times Out:**
- GitHub Actions free tier: 6 hours max
- Native builds typically take 5-10 minutes
- If timeout occurs, check for infinite loops in code

**Push Fails:**
- Verify PAT secret is set correctly
- Check PAT has `repo` and `workflow` scopes
- Ensure PAT hasn't expired
