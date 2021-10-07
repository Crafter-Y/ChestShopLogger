package com.jballou.getshopsigns;

import com.google.gson.*;

import com.jballou.getshopsigns.event.SignUpdateCallback;

import java.lang.reflect.*;
import java.util.*;

import net.fabricmc.api.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.util.math.*;
import net.minecraft.entity.*;
import net.minecraft.entity.decoration.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.SignType;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class GetShopSigns implements ClientModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("getshopsigns");
	public static Hashtable<Integer, ShopSign> shopSigns = new Hashtable<Integer, ShopSign>();
	public static Hashtable<Integer, SignBlockEntity> signs = new Hashtable<Integer, SignBlockEntity>();


	public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	@Override
	public void onInitializeClient() {

		HudRenderCallback.EVENT.register(GetShopSigns::displayBoundingBox);
		SignUpdateCallback.EVENT.register(GetShopSigns::addSign);
		ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, world) -> {
			if (blockEntity instanceof SignBlockEntity) {
				removeSign((SignBlockEntity) blockEntity);
			}
		});
		ClientCommandManager.DISPATCHER.register(
				literal("getshopsigns")
						.executes(context -> {
							String json = gson.toJson(shopSigns.values());
							LOGGER.info(json);

							context.getSource().sendFeedback(new LiteralText(String.format("Processed %d shop signs.",shopSigns.size())));

							/*
							for (ShopSign shopSign : shopSigns.values()) {
								if (shopSign.sellerName != "")
									LOGGER.info(shopSign);
							}
							*/
							return 1;

						})
		);

	}
	private static long lastCalculationTime = 0;
	private static boolean lastCalculationExists = false;
	private static int lastCalculationMinX = 0;
	private static int lastCalculationMinY = 0;
	private static int lastCalculationWidth = 0;
	private static int lastCalculationHeight = 0;

	public static void parseSign(SignBlockEntity signBlockEntity) {
		String[] signText = new String[4];
		for (int i=0; i<4; i++) {
			StringBuilder lineText = new StringBuilder();
			signBlockEntity.getTextOnRow(i).visit((part) -> {
					lineText.append(part);
					return Optional.empty();
				});
			signText[i] = lineText.toString();
		}
		ShopSign shopSign = new ShopSign(signBlockEntity.getPos(),signText);
		if (shopSign.sellerName != "")
			shopSigns.put(signBlockEntity.hashCode(), shopSign);
	}
	public static void addSign(SignBlockEntity sign) {
		try {
			if (signs.containsKey(sign.hashCode())) {
				return;
			}
			LOGGER.info("addSign " + sign.hashCode());
			signs.put(sign.hashCode(), sign);
			parseSign(sign);
		}
		catch (Exception e) {
			LOGGER.error("OOPS! " + e);
		}
	}

	public static void removeSign(SignBlockEntity sign) {
		signs.remove(sign);
	}

	private static void displayBoundingBox(MatrixStack matrixStack, float tickDelta) {
		long currentTime = System.currentTimeMillis();
		if(lastCalculationExists && currentTime - lastCalculationTime < 1000/45) {
			drawHollowFill(matrixStack, lastCalculationMinX, lastCalculationMinY,
					lastCalculationWidth, lastCalculationHeight, 2, 0xffff0000);
			return;
		}

		lastCalculationTime = currentTime;

		MinecraftClient client = MinecraftClient.getInstance();
		int width = client.getWindow().getScaledWidth();
		int height = client.getWindow().getScaledHeight();
		Vec3d cameraDirection = client.cameraEntity.getRotationVec(tickDelta);
		double fov = client.options.fov;
		double angleSize = fov/height;
		Vec3f verticalRotationAxis = new Vec3f(cameraDirection);
		verticalRotationAxis.cross(Vec3f.POSITIVE_Y);
		if(!verticalRotationAxis.normalize()) {
			lastCalculationExists = false;
			return;
		}

		Vec3f horizontalRotationAxis = new Vec3f(cameraDirection);
		horizontalRotationAxis.cross(verticalRotationAxis);
		horizontalRotationAxis.normalize();

		verticalRotationAxis = new Vec3f(cameraDirection);
		verticalRotationAxis.cross(horizontalRotationAxis);

		HitResult hit = client.crosshairTarget;

		if (hit.getType() == HitResult.Type.MISS) {
			lastCalculationExists = false;
			return;
		}

		int minX = width;
		int maxX = 0;
		int minY = height;
		int maxY = 0;

		for(int y = 0; y < height; y +=2) {
			for(int x = 0; x < width; x+=2) {
				if(minX < x && x < maxX && minY < y && y < maxY) {
					continue;
				}

				Vec3d direction = map(
						(float) angleSize,
						cameraDirection,
						horizontalRotationAxis,
						verticalRotationAxis,
						x,
						y,
						width,
						height
				);
				HitResult nextHit = raycastInDirection(client, tickDelta, direction);//TODO make less expensive

				if(nextHit == null) {
					continue;
				}

				if(nextHit.getType() == HitResult.Type.MISS) {
					continue;
				}

				if(nextHit.getType() != hit.getType()) {
					continue;
				}

				if (nextHit.getType() == HitResult.Type.BLOCK) {
					if(!((BlockHitResult) nextHit).getBlockPos().equals(((BlockHitResult) hit).getBlockPos())) {
						continue;
					}
				} else if(nextHit.getType() == HitResult.Type.ENTITY) {
					if(!((EntityHitResult) nextHit).getEntity().equals(((EntityHitResult) hit).getEntity())) {
						continue;
					}
				}

				if(minX > x) minX = x;
				if(minY > y) minY = y;
				if(maxX < x) maxX = x;
				if(maxY < y) maxY = y;
			}
		}


		lastCalculationExists = true;
		lastCalculationMinX = minX;
		lastCalculationMinY = minY;
		lastCalculationWidth = maxX - minX;
		lastCalculationHeight = maxY - minY;

		drawHollowFill(matrixStack, minX, minY, maxX - minX, maxY - minY, 2, 0xffff0000);
		if (hit.getType() == HitResult.Type.BLOCK) {
			BlockPos blockPos = ((BlockHitResult) hit).getBlockPos();
			BlockEntity blockEntity = MinecraftClient.getInstance().world.getBlockEntity(blockPos);
			if (blockEntity instanceof SignBlockEntity) {
				addSign((SignBlockEntity) blockEntity);
			}
		}
		LiteralText text = new LiteralText("Bounding " + minX + " " + minY + " " + width + " " + height + ": ");
		client.player.sendMessage(text.append(getLabel(hit)), true);
	}
	private static void drawHollowFill(MatrixStack matrixStack, int x, int y, int width, int height, int stroke, int color) {
		matrixStack.push();
		matrixStack.translate(x-stroke, y-stroke, 0);
		width += stroke *2;
		height += stroke *2;
		DrawableHelper.fill(matrixStack, 0, 0, width, stroke, color);
		DrawableHelper.fill(matrixStack, width - stroke, 0, width, height, color);
		DrawableHelper.fill(matrixStack, 0, height - stroke, width, height, color);
		DrawableHelper.fill(matrixStack, 0, 0, stroke, height, color);
		matrixStack.pop();
	}

	private static Text getLabel(HitResult hit) {
		if(hit == null) return new LiteralText("null");

		switch (hit.getType()) {
			case BLOCK:
				return getLabelBlock((BlockHitResult) hit);
			case ENTITY:
				return getLabelEntity((EntityHitResult) hit);
			case MISS:
			default:
				return new LiteralText("null");
		}
	}

	private static Text getLabelEntity(EntityHitResult hit) {
		return hit.getEntity().getDisplayName();
	}

	private static Text getLabelBlock(BlockHitResult hit) {
		BlockPos blockPos = hit.getBlockPos();
		BlockState blockState = MinecraftClient.getInstance().world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		return block.getName();
	}

	private static Vec3d map(float anglePerPixel, Vec3d center, Vec3f horizontalRotationAxis,
							 Vec3f verticalRotationAxis, int x, int y, int width, int height) {
		float horizontalRotation = (x - width/2f) * anglePerPixel;
		float verticalRotation = (y - height/2f) * anglePerPixel;

		final Vec3f temp2 = new Vec3f(center);
		temp2.rotate(verticalRotationAxis.getDegreesQuaternion(verticalRotation));
		temp2.rotate(horizontalRotationAxis.getDegreesQuaternion(horizontalRotation));
		return new Vec3d(temp2);
	}

	private static HitResult raycastInDirection(MinecraftClient client, float tickDelta, Vec3d direction) {
		Entity entity = client.getCameraEntity();
		if (entity == null || client.world == null) {
			return null;
		}

		double reachDistance = 5.0F;
		HitResult target = raycast(entity, reachDistance, tickDelta, false, direction);
		boolean tooFar = false;
		double extendedReach = 6.0D;
		reachDistance = extendedReach;

		Vec3d cameraPos = entity.getCameraPosVec(tickDelta);

		extendedReach = extendedReach * extendedReach;
		if (target != null) {
			extendedReach = target.getPos().squaredDistanceTo(cameraPos);
		}

		Vec3d vec3d3 = cameraPos.add(direction.multiply(reachDistance));
		Box box = entity
				.getBoundingBox()
				.stretch(entity.getRotationVec(1.0F).multiply(reachDistance))
				.expand(1.0D, 1.0D, 1.0D);
		EntityHitResult entityHitResult = ProjectileUtil.raycast(
				entity,
				cameraPos,
				vec3d3,
				box,
				(entityx) -> !entityx.isSpectator() && entityx.collides(),
				extendedReach
		);

		if (entityHitResult == null) {
			return target;
		}

		Entity entity2 = entityHitResult.getEntity();
		Vec3d hitPos = entityHitResult.getPos();
		if (cameraPos.squaredDistanceTo(hitPos) < extendedReach || target == null) {
			target = entityHitResult;
			if (entity2 instanceof LivingEntity || entity2 instanceof ItemFrameEntity) {
				client.targetedEntity = entity2;
			}
		}

		return target;
	}

	private static HitResult raycast(
			Entity entity,
			double maxDistance,
			float tickDelta,
			boolean includeFluids,
			Vec3d direction
	) {
		Vec3d end = entity.getCameraPosVec(tickDelta).add(direction.multiply(maxDistance));
		return entity.world.raycast(new RaycastContext(
				entity.getCameraPosVec(tickDelta),
				end,
				RaycastContext.ShapeType.OUTLINE,
				includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE,
				entity
		));
	}
}
