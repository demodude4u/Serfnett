package com.vectorcat.serfnett.tool;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;
import com.vectorcat.serfnett.spi.Service;
import com.vectorcat.serfnett.spi.ServiceNode;
import com.vectorcat.serfnett.spi.ServiceProvider;
import com.vectorcat.serfnett.spi.ServiceRegistry;

public class ServiceNetworkTool {

	public static class ToolNode {
		private static class Links {
			private final Iterable<ServiceNode> serviceNodes;
			private final Function<ServiceNode, ToolNode> transform;

			private Optional<ImmutableList<ToolNode>> toolNodes = Optional
					.absent();

			public Links(Iterable<ServiceNode> nodes,
					Function<ServiceNode, ToolNode> transform) {
				this.serviceNodes = nodes;
				this.transform = transform;

			}

			public ImmutableList<ToolNode> get() {
				if (!toolNodes.isPresent()) {
					Builder<ToolNode> builder = ImmutableList.builder();
					for (ServiceNode serviceNode : serviceNodes) {
						builder.add(transform.apply(serviceNode));
					}
					toolNodes = Optional.of(builder.build());
				}
				return toolNodes.get();
			}
		}

		final ServiceNode serviceNode;

		final boolean isProvider;
		final boolean isRegistry;

		final Links connectedNodes;

		public ToolNode(ServiceNode serviceNode,
				Collection<ServiceNode> connectedNodes,
				Function<ServiceNode, ToolNode> connectedTransform) {
			this.serviceNode = serviceNode;
			this.connectedNodes = new Links(connectedNodes, connectedTransform);

			this.isProvider = serviceNode instanceof ServiceProvider;
			this.isRegistry = serviceNode instanceof ServiceRegistry;
		}

		@Override
		public boolean equals(Object obj) {
			return (obj instanceof ToolNode)
					&& ((ToolNode) obj).serviceNode.equals(serviceNode);
		}

		public ImmutableList<ToolNode> getConnectedNodes() {
			return connectedNodes.get();
		}

		public ServiceNode getServiceNode() {
			return serviceNode;
		}

		@Override
		public int hashCode() {
			return serviceNode.hashCode();
		}

		public boolean isProvider() {
			return isProvider;
		}

		public boolean isRegistry() {
			return isRegistry;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("[" + serviceNode.getClass().getSimpleName() + "]\n"
					+ serviceNode.getDescriptor() + "\n\n");
			if (isProvider()) {
				ServiceProvider provider = (ServiceProvider) serviceNode;
				builder.append("-----Provides-----\n");
				for (Service service : provider.getServices()) {
					builder.append(service.toString() + "\n");
				}
			}
			return builder.toString();
		}
	}

	private final JFrame jFrame;

	private final Collection<ServiceNode> roots;

	private Set<ToolNode> nodes;

	private final mxGraph graph;
	private final mxGraphComponent graphComponent;

	/**
	 * @wbp.parser.entryPoint
	 */
	public ServiceNetworkTool(Collection<ServiceNode> roots) {
		this.roots = roots;
		nodes = createToolNodeGraph(roots);

		graph = new mxGraph();

		graphComponent = new mxGraphComponent(graph);
		graphComponent.setExportEnabled(false);
		graphComponent.setFoldingEnabled(false);
		graphComponent.setPanning(true);

		jFrame = createJFrame(graphComponent);
	}

	private int countLines(String string) {
		return string.split("\r\n|\r|\n").length;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private JFrame createJFrame(Component graph) {
		JFrame jframe = new JFrame();
		jframe.setTitle("Service Network Tool");

		graph.setPreferredSize(new Dimension(512, 512));

		JSplitPane splitPane = new JSplitPane();
		jframe.getContentPane().add(splitPane, BorderLayout.CENTER);

		JPanel panel_1 = new JPanel();
		splitPane.setLeftComponent(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));

		JButton btnRefresh = new JButton("Refresh");
		panel_1.add(btnRefresh);

		splitPane.setRightComponent(graph);

		jframe.pack();
		jframe.setLocationRelativeTo(null);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		return jframe;
	}

	private Set<ToolNode> createToolNodeGraph(Collection<ServiceNode> roots) {
		Set<ToolNode> createdToolNodes = Sets.newLinkedHashSet();

		final Map<ServiceNode, ToolNode> mapLinks = Maps.newHashMap();
		Function<ServiceNode, ToolNode> mapLinksFunction = new Function<ServiceNode, ToolNode>() {
			@Override
			public ToolNode apply(ServiceNode input) {
				return mapLinks.get(input);
			}
		};

		LinkedHashSet<ServiceNode> pendingNodes = Sets.newLinkedHashSet(roots);

		while (!pendingNodes.isEmpty()) {
			ServiceNode serviceNode = pendingNodes.iterator().next();
			pendingNodes.remove(serviceNode);

			@SuppressWarnings("unchecked")
			Collection<ServiceNode> connectedNodes = (Collection<ServiceNode>) serviceNode
					.getConnectedNodes();

			ToolNode toolNode = new ToolNode(serviceNode, connectedNodes,
					mapLinksFunction);
			mapLinks.put(serviceNode, toolNode);
			createdToolNodes.add(toolNode);

			for (ServiceNode connectedNode : connectedNodes) {
				if (!mapLinks.containsKey(connectedNode)) {
					pendingNodes.add(connectedNode);
				}
			}
		}

		return ImmutableSet.copyOf(createdToolNodes);
	}

	public void dispose() {
		jFrame.dispose();
	}

	public Set<ToolNode> getNodes() {
		return nodes;
	}

	public void hide() {
		jFrame.setVisible(false);
	}

	public void refresh() {
		nodes = createToolNodeGraph(roots);

		updateUI();
	}

	public void show() {
		jFrame.setVisible(true);

		updateUI();
	}

	private void updateMxGraph(Set<ToolNode> nodes) {
		mxGraphModel graphModel = new mxGraphModel();

		graph.setModel(graphModel);

		Object parent = graph.getDefaultParent();

		graphModel.beginUpdate();
		try {

			Map<ToolNode, Object> mapVertex = Maps.newHashMap();
			for (ToolNode toolNode : nodes) {
				int lineCount = countLines(toolNode.toString());

				Object vertex = graph.insertVertex(parent, null, toolNode, 0,
						0, 150, 20 + lineCount * 15);
				mapVertex.put(toolNode, vertex);
			}

			for (ToolNode toolNode : nodes) {
				for (ToolNode connectedNode : toolNode.getConnectedNodes()) {
					graph.insertEdge(parent, null, "", mapVertex.get(toolNode),
							mapVertex.get(connectedNode));
				}
			}

		} finally {
			graphModel.endUpdate();
		}

		mxIGraphLayout layout = new mxCompactTreeLayout(graph);

		graph.getModel().beginUpdate();
		try {
			layout.execute(graph.getDefaultParent());
		} finally {
			mxMorphing morph = new mxMorphing(graphComponent, 20, 1.2, 20);

			morph.addListener(mxEvent.DONE, new mxIEventListener() {

				@Override
				public void invoke(Object arg0, mxEventObject arg1) {
					graph.getModel().endUpdate();
				}

			});

			morph.startAnimation();
		}

	}

	private void updateUI() {
		updateMxGraph(nodes);
	}
}
