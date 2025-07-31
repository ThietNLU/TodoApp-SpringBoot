// Todo App JavaScript
class TodoApp {
    constructor() {
        this.todos = [];
        this.editingId = null;
        this.currentSort = 'date';
        this.currentFilters = {
            status: 'all',
            priority: 'all',
            search: ''
        };

        this.init();
    }

    init() {
        this.bindEvents();
        this.loadTodos();
    }

    bindEvents() {
        // Form submission
        document.getElementById('todoForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleFormSubmit();
        });

        // Cancel editing
        document.getElementById('cancelBtn').addEventListener('click', () => {
            this.cancelEdit();
        });

        // Search and filters
        document.getElementById('searchInput').addEventListener('input', (e) => {
            this.currentFilters.search = e.target.value;
            this.filterTodos();
        });

        document.getElementById('filterStatus').addEventListener('change', (e) => {
            this.currentFilters.status = e.target.value;
            this.filterTodos();
        });

        document.getElementById('filterPriority').addEventListener('change', (e) => {
            this.currentFilters.priority = e.target.value;
            this.filterTodos();
        });

        document.getElementById('clearFilters').addEventListener('click', () => {
            this.clearFilters();
        });

        // Sorting
        document.getElementById('sortByDate').addEventListener('click', () => {
            this.sortTodos('date');
        });

        document.getElementById('sortByPriority').addEventListener('click', () => {
            this.sortTodos('priority');
        });
    }

    async loadTodos() {
        try {
            this.showLoading(true);
            const response = await fetch('/api/todos');
            if (response.ok) {
                this.todos = await response.json();
                this.renderTodos();
                this.updateStats();
            } else {
                this.showError('Không thể tải danh sách công việc');
            }
        } catch (error) {
            console.error('Error loading todos:', error);
            this.showError('Lỗi kết nối. Vui lòng thử lại!');
        } finally {
            this.showLoading(false);
        }
    }

    async handleFormSubmit() {
        const formData = this.getFormData();

        if (!formData.title.trim()) {
            this.showError('Vui lòng nhập tiêu đề công việc');
            return;
        }

        try {
            this.showLoading(true);

            if (this.editingId) {
                await this.updateTodo(this.editingId, formData);
            } else {
                await this.createTodo(formData);
            }

            this.resetForm();
            this.loadTodos();
        } catch (error) {
            console.error('Error saving todo:', error);
            this.showError('Không thể lưu công việc. Vui lòng thử lại!');
        } finally {
            this.showLoading(false);
        }
    }

    async createTodo(todoData) {
        const response = await fetch('/api/todos', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(todoData)
        });

        if (response.ok) {
            this.showSuccess('Thêm công việc thành công!');
        } else {
            throw new Error('Failed to create todo');
        }
    }

    async updateTodo(id, todoData) {
        const response = await fetch(`/api/todos/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(todoData)
        });

        if (response.ok) {
            this.showSuccess('Cập nhật công việc thành công!');
        } else {
            throw new Error('Failed to update todo');
        }
    }

    async deleteTodo(id) {
        if (!confirm('Bạn có chắc chắn muốn xóa công việc này?')) {
            return;
        }

        try {
            this.showLoading(true);
            const response = await fetch(`/api/todos/${id}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                this.showSuccess('Xóa công việc thành công!');
                this.loadTodos();
            } else {
                this.showError('Không thể xóa công việc');
            }
        } catch (error) {
            console.error('Error deleting todo:', error);
            this.showError('Lỗi khi xóa công việc');
        } finally {
            this.showLoading(false);
        }
    }

    async toggleComplete(id) {
        try {
            const response = await fetch(`/api/todos/${id}/toggle`, {
                method: 'PATCH'
            });

            if (response.ok) {
                this.loadTodos();
            } else {
                this.showError('Không thể cập nhật trạng thái');
            }
        } catch (error) {
            console.error('Error toggling todo:', error);
            this.showError('Lỗi khi cập nhật trạng thái');
        }
    }

    editTodo(todo) {
        this.editingId = todo.id;

        // Fill form with todo data
        document.getElementById('title').value = todo.title;
        document.getElementById('description').value = todo.description || '';
        document.getElementById('priority').value = todo.priority;

        if (todo.dueDate) {
            const date = new Date(todo.dueDate);
            document.getElementById('dueDate').value = this.formatDateTimeLocal(date);
        }

        // Update form UI
        document.getElementById('submitBtn').innerHTML = '<i class="bi bi-save"></i> Cập nhật';
        document.getElementById('cancelBtn').style.display = 'inline-block';

        // Scroll to form
        document.getElementById('todoForm').scrollIntoView({ behavior: 'smooth' });
    }

    cancelEdit() {
        this.editingId = null;
        this.resetForm();
    }

    resetForm() {
        document.getElementById('todoForm').reset();
        document.getElementById('submitBtn').innerHTML = '<i class="bi bi-plus"></i> Thêm';
        document.getElementById('cancelBtn').style.display = 'none';
        this.editingId = null;
    }

    getFormData() {
        const dueDate = document.getElementById('dueDate').value;
        return {
            title: document.getElementById('title').value.trim(),
            description: document.getElementById('description').value.trim(),
            priority: document.getElementById('priority').value,
            dueDate: dueDate ? new Date(dueDate).toISOString() : null
        };
    }

    renderTodos() {
        const todoList = document.getElementById('todoList');
        const emptyState = document.getElementById('emptyState');

        if (this.todos.length === 0) {
            emptyState.style.display = 'block';
            todoList.innerHTML = '';
            return;
        }

        emptyState.style.display = 'none';

        const filteredTodos = this.getFilteredTodos();

        if (filteredTodos.length === 0) {
            todoList.innerHTML = `
                <div class="text-center py-5">
                    <i class="bi bi-search display-1 text-muted"></i>
                    <p class="text-muted mt-3">Không tìm thấy công việc nào phù hợp với bộ lọc.</p>
                </div>
            `;
            return;
        }

        todoList.innerHTML = filteredTodos.map(todo => this.renderTodoItem(todo)).join('');
    }

    renderTodoItem(todo) {
        const isOverdue = todo.dueDate && new Date(todo.dueDate) < new Date() && !todo.completed;
        const isDueSoon = todo.dueDate && new Date(todo.dueDate) < new Date(Date.now() + 24 * 60 * 60 * 1000) && !todo.completed;

        return `
            <div class="todo-item ${todo.completed ? 'completed' : ''} fade-in">
                <div class="checkbox-wrapper">
                    <input type="checkbox" ${todo.completed ? 'checked' : ''} 
                           onchange="todoApp.toggleComplete(${todo.id})">
                    <div class="flex-grow-1">
                        <div class="todo-title">${this.escapeHtml(todo.title)}</div>
                        ${todo.description ? `<div class="todo-description">${this.escapeHtml(todo.description)}</div>` : ''}
                    </div>
                </div>
                
                <div class="d-flex justify-content-between align-items-center">
                    <div class="todo-meta">
                        <span class="priority-badge priority-${todo.priority}">
                            ${this.getPriorityText(todo.priority)}
                        </span>
                        ${todo.dueDate ? `
                            <span class="due-date ms-2 ${isOverdue ? 'overdue' : isDueSoon ? 'due-soon' : ''}">
                                <i class="bi bi-calendar"></i> ${this.formatDate(todo.dueDate)}
                            </span>
                        ` : ''}
                        <small class="ms-2 text-muted">
                            <i class="bi bi-clock"></i> ${this.formatDate(todo.createdAt)}
                        </small>
                    </div>
                    
                    <div class="todo-actions">
                        <button type="button" class="btn btn-outline-primary btn-sm" 
                                onclick="todoApp.editTodo(${JSON.stringify(todo).replace(/"/g, '&quot;')})">
                            <i class="bi bi-pencil"></i> Sửa
                        </button>
                        <button type="button" class="btn btn-outline-danger btn-sm" 
                                onclick="todoApp.deleteTodo(${todo.id})">
                            <i class="bi bi-trash"></i> Xóa
                        </button>
                    </div>
                </div>
            </div>
        `;
    }

    getFilteredTodos() {
        return this.todos.filter(todo => {
            // Status filter
            if (this.currentFilters.status === 'completed' && !todo.completed) return false;
            if (this.currentFilters.status === 'pending' && todo.completed) return false;

            // Priority filter
            if (this.currentFilters.priority !== 'all' && todo.priority !== this.currentFilters.priority) return false;

            // Search filter
            if (this.currentFilters.search) {
                const search = this.currentFilters.search.toLowerCase();
                const title = todo.title.toLowerCase();
                const description = (todo.description || '').toLowerCase();
                if (!title.includes(search) && !description.includes(search)) return false;
            }

            return true;
        });
    }

    sortTodos(sortBy) {
        this.currentSort = sortBy;

        this.todos.sort((a, b) => {
            if (sortBy === 'date') {
                return new Date(b.createdAt) - new Date(a.createdAt);
            } else if (sortBy === 'priority') {
                const priorityOrder = { 'URGENT': 4, 'HIGH': 3, 'NORMAL': 2, 'LOW': 1 };
                return priorityOrder[b.priority] - priorityOrder[a.priority];
            }
            return 0;
        });

        this.renderTodos();
    }

    filterTodos() {
        this.renderTodos();
    }

    clearFilters() {
        this.currentFilters = {
            status: 'all',
            priority: 'all',
            search: ''
        };

        document.getElementById('searchInput').value = '';
        document.getElementById('filterStatus').value = 'all';
        document.getElementById('filterPriority').value = 'all';

        this.renderTodos();
    }

    updateStats() {
        const total = this.todos.length;
        const completed = this.todos.filter(t => t.completed).length;
        const pending = total - completed;

        document.getElementById('taskCount').textContent = total;
    }

    // Utility methods
    formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleString('vi-VN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    formatDateTimeLocal(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');

        return `${year}-${month}-${day}T${hours}:${minutes}`;
    }

    getPriorityText(priority) {
        const priorityTexts = {
            'LOW': 'Thấp',
            'NORMAL': 'Bình thường',
            'HIGH': 'Cao',
            'URGENT': 'Khẩn cấp'
        };
        return priorityTexts[priority] || priority;
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    showLoading(show) {
        const spinner = document.getElementById('loadingSpinner');
        spinner.style.display = show ? 'block' : 'none';
    }

    showToast(message, type = 'info') {
        const toastContainer = document.getElementById('toastContainer');
        const toastId = 'toast-' + Date.now();

        const toastHtml = `
            <div id="${toastId}" class="toast" role="alert">
                <div class="toast-header">
                    <i class="bi bi-${type === 'success' ? 'check-circle text-success' : type === 'error' ? 'exclamation-triangle text-danger' : 'info-circle text-info'}"></i>
                    <strong class="me-auto ms-2">Thông báo</strong>
                    <button type="button" class="btn-close" data-bs-dismiss="toast"></button>
                </div>
                <div class="toast-body">
                    ${message}
                </div>
            </div>
        `;

        toastContainer.insertAdjacentHTML('beforeend', toastHtml);

        const toastElement = document.getElementById(toastId);
        const toast = new bootstrap.Toast(toastElement);
        toast.show();

        // Remove toast element after it's hidden
        toastElement.addEventListener('hidden.bs.toast', () => {
            toastElement.remove();
        });
    }

    showSuccess(message) {
        this.showToast(message, 'success');
    }

    showError(message) {
        this.showToast(message, 'error');
    }

    showInfo(message) {
        this.showToast(message, 'info');
    }
}

// Initialize the app when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.todoApp = new TodoApp();
});

