package com.github.lobo.less.daemon.ui;

import java.awt.Component;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.github.lobo.less.daemon.FolderManager;
import com.github.lobo.less.daemon.event.AddFolderEvent;
import com.github.lobo.less.daemon.event.AddImportEvent;
import com.github.lobo.less.daemon.event.RemoveFolderEvent;
import com.github.lobo.less.daemon.event.RemoveImportEvent;
import com.github.lobo.less.daemon.model.LessContainer;
import com.github.lobo.less.daemon.model.LessFile;
import com.github.lobo.less.daemon.model.LessFolder;
import com.github.lobo.less.daemon.resources.Icons;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

@SuppressWarnings("serial")
public class DepencencyTree extends JTree {

	private List<LessFolder> folders = Lists.newArrayList();

	private static final String ROOT_NODE = "/";

	private Map<LessContainer, LessContainerTreeNode> nodeMap = Maps.newConcurrentMap();
	
	private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(ROOT_NODE);

	private DefaultTreeModel treeModel;

	private DefaultTreeSelectionModel selectionModel;

	class LessTreeModel implements TreeModel {

		@Override
		public Object getRoot() {
			return ROOT_NODE;
		}

		@Override
		public Object getChild(Object parent, int index) {
			if (ROOT_NODE.equals(parent)) return folders.get(index);

			if (parent instanceof LessContainer) {
				LessContainer folder = (LessContainer) parent;
				return folder.getFiles().get(index);
			}

			return null;
		}

		@Override
		public int getChildCount(Object parent) {
			if (ROOT_NODE.equals(parent)) return folders.size();

			if (parent instanceof LessContainer) {
				LessContainer folder = (LessContainer) parent;
				return folder.getFiles().size();
			}

			return 0;
		}

		@Override
		public boolean isLeaf(Object node) {
			if (ROOT_NODE.equals(node)) return folders.isEmpty();

			if (node instanceof LessContainer) {
				LessContainer folder = (LessContainer) node;
				return folder.getFiles().isEmpty();
			}

			return false;
		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue) {

		}

		@Override
		public int getIndexOfChild(Object parent, Object child) {
			if (ROOT_NODE.equals(parent)) return folders.indexOf(child);

			if (parent instanceof LessContainer) {
				LessContainer folder = (LessContainer) parent;
				return folder.getFiles().indexOf(child);
			}

			return 0;
		}

		@Override
		public void addTreeModelListener(TreeModelListener l) {

		}

		@Override
		public void removeTreeModelListener(TreeModelListener l) {

		}

	}

	class LessCellRenderer extends DefaultTreeCellRenderer {

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

			if (value instanceof LessContainerTreeNode) {
				LessContainerTreeNode node = (LessContainerTreeNode) value;
				if (node.isFile()) {
					LessFile lessFile = node.as();
					setIcon(lessFile.isRoot() ? Icons.LESS_ICON : Icons.IMPORT_ICON);
					Path lessFilePath = Paths.get(lessFile.getFilename());
					setText(lessFilePath.toFile().getName() + " (" + lessFile.getFiles().size() + ")");
				}

				if (node.isFolder()) {
					LessFolder lessFolder = node.as();
					setIcon(Icons.FOLDER_ICON);
					setText(lessFolder.getFilename() + " (" + lessFolder.getFiles().size() + ")");
				}
			}

			return this;
		}
	}

	class LessContainerTreeNode extends DefaultMutableTreeNode {
		public LessContainerTreeNode(LessContainer container) {
			super(container);
		}

		private LessContainer container() {
			return (LessContainer) getUserObject();
		}

		public <T extends LessContainer> T as() {
			return container().as();
		}

		public boolean isFolder() {
			return container().isFolder();
		}

		public boolean isFile() {
			return container().isFile();
		}
	}


	@Inject
	public DepencencyTree(EventBus eventBus, FolderManager folderManager) {
		eventBus.register(this);
		treeModel = new DefaultTreeModel(rootNode);
		selectionModel = new DefaultTreeSelectionModel();
		setModel(treeModel);
		setSelectionModel(selectionModel);
		setCellRenderer(new LessCellRenderer());
		for(LessFolder folder : folderManager.getFolderList())
			addFolder(folder);
	}
	

	@Subscribe
	public void onAddFolder(AddFolderEvent event) {
		LessFolder folder = event.getFolder();
		addFolder(folder);
	}

	private void addFolder(LessFolder folder) {
		LessContainerTreeNode node = addChild(folder, rootNode);
		if(node != null) {
			walkFiles(folder);
			TreeNode[] nodePath = treeModel.getPathToRoot(node);
			TreePath path = new TreePath(nodePath);
			setSelectionPath(path);
			expandPath(path);
		}
	}

	@Subscribe
	public void onRemoveFolder(RemoveFolderEvent event) {
		LessFolder folder = event.getFolder();
		removeNode(folder);
	}

	private void walkFiles(LessContainer parent) {
		for (LessFile child : parent.getFiles()) {
			addChild(child, parent);
			walkFiles(child);
		}
	}

	@Subscribe
	public void onAddImport(AddImportEvent event) {
		LessFile file = event.getFile();
		LessContainer parent = file.getParent();
		LessContainerTreeNode node = addChild(file, parent);
		if (node != null) {
			TreeNode[] nodePath = treeModel.getPathToRoot(node);
			TreePath path = new TreePath(nodePath);
			setSelectionPath(path);
			expandPath(path);
		}
	}

	@Subscribe
	public void onRemoveImport(RemoveImportEvent event) {
		LessFile file = event.getFile();
		removeNode(file);
	}

	private void removeNode(LessContainer container) {
		LessContainerTreeNode node = nodeMap.get(container);
		if (node != null)
			node.removeFromParent();
	}

	private LessContainerTreeNode addChild(LessContainer child, LessContainer parent) {
		LessContainerTreeNode parentNode = nodeMap.get(parent);
		if (parentNode != null) return addChild(child, parentNode);
		return null;
	}

	private LessContainerTreeNode addChild(LessContainer child, MutableTreeNode parentNode) {
		LessContainerTreeNode fileNode = new LessContainerTreeNode(child);
		nodeMap.put(child, fileNode);
		treeModel.insertNodeInto(fileNode, parentNode, 0);
		return fileNode;
	}
	
}
