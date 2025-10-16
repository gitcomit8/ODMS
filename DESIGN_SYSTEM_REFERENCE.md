# 🎨 Design System Quick Reference

## Color Variables
```css
--primary-gradient:    linear-gradient(135deg, #667eea 0%, #764ba2 100%)
--secondary-gradient:  linear-gradient(135deg, #f093fb 0%, #f5576c 100%)
--success-gradient:    linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)
--danger-gradient:     linear-gradient(135deg, #fa709a 0%, #fee140 100%)
--light-bg:           #f8f9ff
--card-bg:            #ffffff
--text-primary:       #2d3748
--text-secondary:     #718096
--border-radius:      16px
--box-shadow:         0 10px 40px rgba(0, 0, 0, 0.1)
--transition:         all 0.3s cubic-bezier(0.4, 0, 0.2, 1)
```

## Button Classes
```html
<button class="btn btn-primary">Primary Action</button>
<button class="btn btn-success">Success Action</button>
<button class="btn btn-danger">Danger Action</button>
<button class="btn btn-secondary">Secondary Action</button>
```

## Card Component
```html
<div class="card">
  <h2>Card Title</h2>
  <p>Card content...</p>
</div>
```

## Status Badges
```html
<span class="status status-SUBMITTED">Submitted</span>
<span class="status status-APPROVED">Approved</span>
<span class="status status-REJECTED">Rejected</span>
```

## Message Alerts
```html
<div class="message success">Success message</div>
<div class="message error">Error message</div>
```

## Modal Structure
```html
<div class="modal" id="myModal">
  <div class="modal-content">
    <span class="close" onclick="closeModal()">&times;</span>
    <h2>Modal Title</h2>
    <!-- Modal content -->
  </div>
</div>
```

## Form Groups
```html
<div class="form-group">
  <label for="input">Label</label>
  <input type="text" id="input" placeholder="Placeholder">
</div>
```

## Navigation Links
```html
<div class="nav-links">
  <a href="/path" class="btn btn-primary">Link 1</a>
  <a href="/path" class="btn btn-secondary">Link 2</a>
</div>
```

## Animation Classes
- `fadeIn` - Fade in animation
- `fadeInUp` - Fade in from bottom
- `fadeInDown` - Fade in from top
- `slideIn` - Slide in from left
- `zoomIn` - Zoom in scale effect
- `bounce` - Continuous bounce
- `pulse` - Pulsing effect

## Utility Classes
```css
.text-center   /* Center align text */
.mt-1, .mt-2, .mt-3  /* Margin top */
.mb-1, .mb-2, .mb-3  /* Margin bottom */
.p-1, .p-2, .p-3     /* Padding */
```

## Responsive Grid
```html
<div class="event-details">
  <!-- Auto-fits to screen size -->
  <div class="form-group">...</div>
  <div class="form-group">...</div>
</div>
```

## Icon Usage
Use emojis for visual elements:
- 🎓 Education
- 📊 Data/Analytics
- ✅ Success/Approved
- ⚠️ Warning/Error
- 📝 Forms/Input
- 👥 Users/People
- 🏠 Home
- 🔧 Settings/Tools
- 📋 Lists/Documents
- ⏳ Pending/Waiting

## Empty State Template
```html
<div class="empty-state">
  <div class="empty-state-icon">📭</div>
  <div>No items found</div>
</div>
```

## Table with Modern Style
```html
<table>
  <thead>
    <tr>
      <th>Column 1</th>
      <th>Column 2</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Data 1</td>
      <td>Data 2</td>
    </tr>
  </tbody>
</table>
```

## Link CSS in Template
```html
<link rel="stylesheet" th:href="@{/css/modern-style.css}">
```

## Gradient Background Page
```css
body {
    background: linear-gradient(-45deg, #667eea, #764ba2, #f093fb, #4facfe);
    background-size: 400% 400%;
    animation: gradientShift 15s ease infinite;
}
```

## Count Badge
```html
<span class="count-badge">25</span>
```

## Action Buttons
```html
<button class="approve-btn">Approve</button>
<button class="reject-btn">Reject</button>
<button class="btn-add">Add</button>
<button class="btn-remove">Remove</button>
```

## Dashboard Section
```html
<div class="dashboard-section">
  <h2>Section Title</h2>
  <!-- Section content -->
</div>
```

## Tips
1. Always include viewport meta tag
2. Use semantic HTML5 elements
3. Link modern-style.css in all pages
4. Use emojis for visual appeal
5. Apply animations sparingly
6. Test on mobile devices
7. Maintain consistent spacing
8. Use gradient backgrounds for impact

---
**Pro Tip**: All animations are CSS-only for best performance!
