// Code generated by protoc-gen-go. DO NOT EDIT.
// versions:
// 	protoc-gen-go v1.28.1
// 	protoc        v3.21.5
// source: video/coordinator/stat_v1/stat.proto

package stat_v1

import (
	_ "github.com/envoyproxy/protoc-gen-validate/validate"
	protoreflect "google.golang.org/protobuf/reflect/protoreflect"
	protoimpl "google.golang.org/protobuf/runtime/protoimpl"
	durationpb "google.golang.org/protobuf/types/known/durationpb"
	timestamppb "google.golang.org/protobuf/types/known/timestamppb"
	reflect "reflect"
	sync "sync"
)

const (
	// Verify that this generated code is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(20 - protoimpl.MinVersion)
	// Verify that runtime/protoimpl is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(protoimpl.MaxVersion - 20)
)

type MediaType int32

const (
	MediaType_MEDIA_TYPE_UNSPECIFIED MediaType = 0
	MediaType_MEDIA_TYPE_AUDIO       MediaType = 1
	MediaType_MEDIA_TYPE_VIDEO       MediaType = 2
)

// Enum value maps for MediaType.
var (
	MediaType_name = map[int32]string{
		0: "MEDIA_TYPE_UNSPECIFIED",
		1: "MEDIA_TYPE_AUDIO",
		2: "MEDIA_TYPE_VIDEO",
	}
	MediaType_value = map[string]int32{
		"MEDIA_TYPE_UNSPECIFIED": 0,
		"MEDIA_TYPE_AUDIO":       1,
		"MEDIA_TYPE_VIDEO":       2,
	}
)

func (x MediaType) Enum() *MediaType {
	p := new(MediaType)
	*p = x
	return p
}

func (x MediaType) String() string {
	return protoimpl.X.EnumStringOf(x.Descriptor(), protoreflect.EnumNumber(x))
}

func (MediaType) Descriptor() protoreflect.EnumDescriptor {
	return file_video_coordinator_stat_v1_stat_proto_enumTypes[0].Descriptor()
}

func (MediaType) Type() protoreflect.EnumType {
	return &file_video_coordinator_stat_v1_stat_proto_enumTypes[0]
}

func (x MediaType) Number() protoreflect.EnumNumber {
	return protoreflect.EnumNumber(x)
}

// Deprecated: Use MediaType.Descriptor instead.
func (MediaType) EnumDescriptor() ([]byte, []int) {
	return file_video_coordinator_stat_v1_stat_proto_rawDescGZIP(), []int{0}
}

type MediaStateChange int32

const (
	MediaStateChange_MEDIA_STATE_CHANGE_UNSPECIFIED MediaStateChange = 0
	MediaStateChange_MEDIA_STATE_CHANGE_STARTED     MediaStateChange = 1
	MediaStateChange_MEDIA_STATE_CHANGE_ENDED       MediaStateChange = 2
)

// Enum value maps for MediaStateChange.
var (
	MediaStateChange_name = map[int32]string{
		0: "MEDIA_STATE_CHANGE_UNSPECIFIED",
		1: "MEDIA_STATE_CHANGE_STARTED",
		2: "MEDIA_STATE_CHANGE_ENDED",
	}
	MediaStateChange_value = map[string]int32{
		"MEDIA_STATE_CHANGE_UNSPECIFIED": 0,
		"MEDIA_STATE_CHANGE_STARTED":     1,
		"MEDIA_STATE_CHANGE_ENDED":       2,
	}
)

func (x MediaStateChange) Enum() *MediaStateChange {
	p := new(MediaStateChange)
	*p = x
	return p
}

func (x MediaStateChange) String() string {
	return protoimpl.X.EnumStringOf(x.Descriptor(), protoreflect.EnumNumber(x))
}

func (MediaStateChange) Descriptor() protoreflect.EnumDescriptor {
	return file_video_coordinator_stat_v1_stat_proto_enumTypes[1].Descriptor()
}

func (MediaStateChange) Type() protoreflect.EnumType {
	return &file_video_coordinator_stat_v1_stat_proto_enumTypes[1]
}

func (x MediaStateChange) Number() protoreflect.EnumNumber {
	return protoreflect.EnumNumber(x)
}

// Deprecated: Use MediaStateChange.Descriptor instead.
func (MediaStateChange) EnumDescriptor() ([]byte, []int) {
	return file_video_coordinator_stat_v1_stat_proto_rawDescGZIP(), []int{1}
}

type MediaDirection int32

const (
	MediaDirection_MEDIA_DIRECTION_UNSPECIFIED MediaDirection = 0
	MediaDirection_MEDIA_DIRECTION_SEND        MediaDirection = 1
	MediaDirection_MEDIA_DIRECTION_RECEIVE     MediaDirection = 2
)

// Enum value maps for MediaDirection.
var (
	MediaDirection_name = map[int32]string{
		0: "MEDIA_DIRECTION_UNSPECIFIED",
		1: "MEDIA_DIRECTION_SEND",
		2: "MEDIA_DIRECTION_RECEIVE",
	}
	MediaDirection_value = map[string]int32{
		"MEDIA_DIRECTION_UNSPECIFIED": 0,
		"MEDIA_DIRECTION_SEND":        1,
		"MEDIA_DIRECTION_RECEIVE":     2,
	}
)

func (x MediaDirection) Enum() *MediaDirection {
	p := new(MediaDirection)
	*p = x
	return p
}

func (x MediaDirection) String() string {
	return protoimpl.X.EnumStringOf(x.Descriptor(), protoreflect.EnumNumber(x))
}

func (MediaDirection) Descriptor() protoreflect.EnumDescriptor {
	return file_video_coordinator_stat_v1_stat_proto_enumTypes[2].Descriptor()
}

func (MediaDirection) Type() protoreflect.EnumType {
	return &file_video_coordinator_stat_v1_stat_proto_enumTypes[2]
}

func (x MediaDirection) Number() protoreflect.EnumNumber {
	return protoreflect.EnumNumber(x)
}

// Deprecated: Use MediaDirection.Descriptor instead.
func (MediaDirection) EnumDescriptor() ([]byte, []int) {
	return file_video_coordinator_stat_v1_stat_proto_rawDescGZIP(), []int{2}
}

type MediaStateChangeReason int32

const (
	MediaStateChangeReason_MEDIA_STATE_CHANGE_REASON_UNSPECIFIED MediaStateChangeReason = 0
	MediaStateChangeReason_MEDIA_STATE_CHANGE_REASON_MUTE        MediaStateChangeReason = 1
	MediaStateChangeReason_MEDIA_STATE_CHANGE_REASON_CONNECTION  MediaStateChangeReason = 2
)

// Enum value maps for MediaStateChangeReason.
var (
	MediaStateChangeReason_name = map[int32]string{
		0: "MEDIA_STATE_CHANGE_REASON_UNSPECIFIED",
		1: "MEDIA_STATE_CHANGE_REASON_MUTE",
		2: "MEDIA_STATE_CHANGE_REASON_CONNECTION",
	}
	MediaStateChangeReason_value = map[string]int32{
		"MEDIA_STATE_CHANGE_REASON_UNSPECIFIED": 0,
		"MEDIA_STATE_CHANGE_REASON_MUTE":        1,
		"MEDIA_STATE_CHANGE_REASON_CONNECTION":  2,
	}
)

func (x MediaStateChangeReason) Enum() *MediaStateChangeReason {
	p := new(MediaStateChangeReason)
	*p = x
	return p
}

func (x MediaStateChangeReason) String() string {
	return protoimpl.X.EnumStringOf(x.Descriptor(), protoreflect.EnumNumber(x))
}

func (MediaStateChangeReason) Descriptor() protoreflect.EnumDescriptor {
	return file_video_coordinator_stat_v1_stat_proto_enumTypes[3].Descriptor()
}

func (MediaStateChangeReason) Type() protoreflect.EnumType {
	return &file_video_coordinator_stat_v1_stat_proto_enumTypes[3]
}

func (x MediaStateChangeReason) Number() protoreflect.EnumNumber {
	return protoreflect.EnumNumber(x)
}

// Deprecated: Use MediaStateChangeReason.Descriptor instead.
func (MediaStateChangeReason) EnumDescriptor() ([]byte, []int) {
	return file_video_coordinator_stat_v1_stat_proto_rawDescGZIP(), []int{3}
}

// ParticipantConnected is fired when a user joins a call
type ParticipantConnected struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields
}

func (x *ParticipantConnected) Reset() {
	*x = ParticipantConnected{}
	if protoimpl.UnsafeEnabled {
		mi := &file_video_coordinator_stat_v1_stat_proto_msgTypes[0]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *ParticipantConnected) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*ParticipantConnected) ProtoMessage() {}

func (x *ParticipantConnected) ProtoReflect() protoreflect.Message {
	mi := &file_video_coordinator_stat_v1_stat_proto_msgTypes[0]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use ParticipantConnected.ProtoReflect.Descriptor instead.
func (*ParticipantConnected) Descriptor() ([]byte, []int) {
	return file_video_coordinator_stat_v1_stat_proto_rawDescGZIP(), []int{0}
}

// ParticipantDisconnected is fired when a user leaves a call
type ParticipantDisconnected struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields
}

func (x *ParticipantDisconnected) Reset() {
	*x = ParticipantDisconnected{}
	if protoimpl.UnsafeEnabled {
		mi := &file_video_coordinator_stat_v1_stat_proto_msgTypes[1]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *ParticipantDisconnected) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*ParticipantDisconnected) ProtoMessage() {}

func (x *ParticipantDisconnected) ProtoReflect() protoreflect.Message {
	mi := &file_video_coordinator_stat_v1_stat_proto_msgTypes[1]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use ParticipantDisconnected.ProtoReflect.Descriptor instead.
func (*ParticipantDisconnected) Descriptor() ([]byte, []int) {
	return file_video_coordinator_stat_v1_stat_proto_rawDescGZIP(), []int{1}
}

// The participant experienced a significant amount of audio/video freeze when observing a given peer
type Freeze struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	MediaType MediaType `protobuf:"varint,1,opt,name=media_type,json=mediaType,proto3,enum=stream.video.coordinator.stat_v1.MediaType" json:"media_type,omitempty"`
	// Sender of the media stream
	PeerId   string               `protobuf:"bytes,2,opt,name=peer_id,json=peerId,proto3" json:"peer_id,omitempty"`
	Duration *durationpb.Duration `protobuf:"bytes,3,opt,name=duration,proto3" json:"duration,omitempty"`
}

func (x *Freeze) Reset() {
	*x = Freeze{}
	if protoimpl.UnsafeEnabled {
		mi := &file_video_coordinator_stat_v1_stat_proto_msgTypes[2]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *Freeze) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*Freeze) ProtoMessage() {}

func (x *Freeze) ProtoReflect() protoreflect.Message {
	mi := &file_video_coordinator_stat_v1_stat_proto_msgTypes[2]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use Freeze.ProtoReflect.Descriptor instead.
func (*Freeze) Descriptor() ([]byte, []int) {
	return file_video_coordinator_stat_v1_stat_proto_rawDescGZIP(), []int{2}
}

func (x *Freeze) GetMediaType() MediaType {
	if x != nil {
		return x.MediaType
	}
	return MediaType_MEDIA_TYPE_UNSPECIFIED
}

func (x *Freeze) GetPeerId() string {
	if x != nil {
		return x.PeerId
	}
	return ""
}

func (x *Freeze) GetDuration() *durationpb.Duration {
	if x != nil {
		return x.Duration
	}
	return nil
}

// A participant started/ended sending/receiving audio/video for a given reason
type MediaStateChanged struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	MediaType MediaType              `protobuf:"varint,1,opt,name=media_type,json=mediaType,proto3,enum=stream.video.coordinator.stat_v1.MediaType" json:"media_type,omitempty"`
	Change    MediaStateChange       `protobuf:"varint,2,opt,name=change,proto3,enum=stream.video.coordinator.stat_v1.MediaStateChange" json:"change,omitempty"`
	Direction MediaDirection         `protobuf:"varint,3,opt,name=direction,proto3,enum=stream.video.coordinator.stat_v1.MediaDirection" json:"direction,omitempty"`
	Reason    MediaStateChangeReason `protobuf:"varint,4,opt,name=reason,proto3,enum=stream.video.coordinator.stat_v1.MediaStateChangeReason" json:"reason,omitempty"`
}

func (x *MediaStateChanged) Reset() {
	*x = MediaStateChanged{}
	if protoimpl.UnsafeEnabled {
		mi := &file_video_coordinator_stat_v1_stat_proto_msgTypes[3]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *MediaStateChanged) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*MediaStateChanged) ProtoMessage() {}

func (x *MediaStateChanged) ProtoReflect() protoreflect.Message {
	mi := &file_video_coordinator_stat_v1_stat_proto_msgTypes[3]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use MediaStateChanged.ProtoReflect.Descriptor instead.
func (*MediaStateChanged) Descriptor() ([]byte, []int) {
	return file_video_coordinator_stat_v1_stat_proto_rawDescGZIP(), []int{3}
}

func (x *MediaStateChanged) GetMediaType() MediaType {
	if x != nil {
		return x.MediaType
	}
	return MediaType_MEDIA_TYPE_UNSPECIFIED
}

func (x *MediaStateChanged) GetChange() MediaStateChange {
	if x != nil {
		return x.Change
	}
	return MediaStateChange_MEDIA_STATE_CHANGE_UNSPECIFIED
}

func (x *MediaStateChanged) GetDirection() MediaDirection {
	if x != nil {
		return x.Direction
	}
	return MediaDirection_MEDIA_DIRECTION_UNSPECIFIED
}

func (x *MediaStateChanged) GetReason() MediaStateChangeReason {
	if x != nil {
		return x.Reason
	}
	return MediaStateChangeReason_MEDIA_STATE_CHANGE_REASON_UNSPECIFIED
}

// A stat event from the perspective of a particular participant
type TelemetryEvent struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	// Event timestamp as RFC3339 string.
	Timestamp *timestamppb.Timestamp `protobuf:"bytes,1,opt,name=timestamp,proto3" json:"timestamp,omitempty"`
	// Types that are assignable to Event:
	//
	//	*TelemetryEvent_ParticipantConnected
	//	*TelemetryEvent_ParticipantDisconnected
	//	*TelemetryEvent_MediaStateChanged
	//	*TelemetryEvent_Freeze
	Event isTelemetryEvent_Event `protobuf_oneof:"event"`
}

func (x *TelemetryEvent) Reset() {
	*x = TelemetryEvent{}
	if protoimpl.UnsafeEnabled {
		mi := &file_video_coordinator_stat_v1_stat_proto_msgTypes[4]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *TelemetryEvent) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*TelemetryEvent) ProtoMessage() {}

func (x *TelemetryEvent) ProtoReflect() protoreflect.Message {
	mi := &file_video_coordinator_stat_v1_stat_proto_msgTypes[4]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use TelemetryEvent.ProtoReflect.Descriptor instead.
func (*TelemetryEvent) Descriptor() ([]byte, []int) {
	return file_video_coordinator_stat_v1_stat_proto_rawDescGZIP(), []int{4}
}

func (x *TelemetryEvent) GetTimestamp() *timestamppb.Timestamp {
	if x != nil {
		return x.Timestamp
	}
	return nil
}

func (m *TelemetryEvent) GetEvent() isTelemetryEvent_Event {
	if m != nil {
		return m.Event
	}
	return nil
}

func (x *TelemetryEvent) GetParticipantConnected() *ParticipantConnected {
	if x, ok := x.GetEvent().(*TelemetryEvent_ParticipantConnected); ok {
		return x.ParticipantConnected
	}
	return nil
}

func (x *TelemetryEvent) GetParticipantDisconnected() *ParticipantDisconnected {
	if x, ok := x.GetEvent().(*TelemetryEvent_ParticipantDisconnected); ok {
		return x.ParticipantDisconnected
	}
	return nil
}

func (x *TelemetryEvent) GetMediaStateChanged() *MediaStateChanged {
	if x, ok := x.GetEvent().(*TelemetryEvent_MediaStateChanged); ok {
		return x.MediaStateChanged
	}
	return nil
}

func (x *TelemetryEvent) GetFreeze() *Freeze {
	if x, ok := x.GetEvent().(*TelemetryEvent_Freeze); ok {
		return x.Freeze
	}
	return nil
}

type isTelemetryEvent_Event interface {
	isTelemetryEvent_Event()
}

type TelemetryEvent_ParticipantConnected struct {
	ParticipantConnected *ParticipantConnected `protobuf:"bytes,2,opt,name=participant_connected,json=participantConnected,proto3,oneof"`
}

type TelemetryEvent_ParticipantDisconnected struct {
	ParticipantDisconnected *ParticipantDisconnected `protobuf:"bytes,3,opt,name=participant_disconnected,json=participantDisconnected,proto3,oneof"`
}

type TelemetryEvent_MediaStateChanged struct {
	MediaStateChanged *MediaStateChanged `protobuf:"bytes,4,opt,name=media_state_changed,json=mediaStateChanged,proto3,oneof"`
}

type TelemetryEvent_Freeze struct {
	Freeze *Freeze `protobuf:"bytes,5,opt,name=freeze,proto3,oneof"`
}

func (*TelemetryEvent_ParticipantConnected) isTelemetryEvent_Event() {}

func (*TelemetryEvent_ParticipantDisconnected) isTelemetryEvent_Event() {}

func (*TelemetryEvent_MediaStateChanged) isTelemetryEvent_Event() {}

func (*TelemetryEvent_Freeze) isTelemetryEvent_Event() {}

type CallParticipantTimeline struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	// The events in this timeline are from the perspective of the user with this ID
	UserId    string            `protobuf:"bytes,1,opt,name=user_id,json=userId,proto3" json:"user_id,omitempty"`
	SessionId string            `protobuf:"bytes,2,opt,name=session_id,json=sessionId,proto3" json:"session_id,omitempty"`
	Events    []*TelemetryEvent `protobuf:"bytes,3,rep,name=events,proto3" json:"events,omitempty"`
}

func (x *CallParticipantTimeline) Reset() {
	*x = CallParticipantTimeline{}
	if protoimpl.UnsafeEnabled {
		mi := &file_video_coordinator_stat_v1_stat_proto_msgTypes[5]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *CallParticipantTimeline) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*CallParticipantTimeline) ProtoMessage() {}

func (x *CallParticipantTimeline) ProtoReflect() protoreflect.Message {
	mi := &file_video_coordinator_stat_v1_stat_proto_msgTypes[5]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use CallParticipantTimeline.ProtoReflect.Descriptor instead.
func (*CallParticipantTimeline) Descriptor() ([]byte, []int) {
	return file_video_coordinator_stat_v1_stat_proto_rawDescGZIP(), []int{5}
}

func (x *CallParticipantTimeline) GetUserId() string {
	if x != nil {
		return x.UserId
	}
	return ""
}

func (x *CallParticipantTimeline) GetSessionId() string {
	if x != nil {
		return x.SessionId
	}
	return ""
}

func (x *CallParticipantTimeline) GetEvents() []*TelemetryEvent {
	if x != nil {
		return x.Events
	}
	return nil
}

var File_video_coordinator_stat_v1_stat_proto protoreflect.FileDescriptor

var file_video_coordinator_stat_v1_stat_proto_rawDesc = []byte{
	0x0a, 0x24, 0x76, 0x69, 0x64, 0x65, 0x6f, 0x2f, 0x63, 0x6f, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x61,
	0x74, 0x6f, 0x72, 0x2f, 0x73, 0x74, 0x61, 0x74, 0x5f, 0x76, 0x31, 0x2f, 0x73, 0x74, 0x61, 0x74,
	0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x12, 0x20, 0x73, 0x74, 0x72, 0x65, 0x61, 0x6d, 0x2e, 0x76,
	0x69, 0x64, 0x65, 0x6f, 0x2e, 0x63, 0x6f, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x61, 0x74, 0x6f, 0x72,
	0x2e, 0x73, 0x74, 0x61, 0x74, 0x5f, 0x76, 0x31, 0x1a, 0x1e, 0x67, 0x6f, 0x6f, 0x67, 0x6c, 0x65,
	0x2f, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x62, 0x75, 0x66, 0x2f, 0x64, 0x75, 0x72, 0x61, 0x74, 0x69,
	0x6f, 0x6e, 0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x1a, 0x1f, 0x67, 0x6f, 0x6f, 0x67, 0x6c, 0x65,
	0x2f, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x62, 0x75, 0x66, 0x2f, 0x74, 0x69, 0x6d, 0x65, 0x73, 0x74,
	0x61, 0x6d, 0x70, 0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x1a, 0x17, 0x76, 0x61, 0x6c, 0x69, 0x64,
	0x61, 0x74, 0x65, 0x2f, 0x76, 0x61, 0x6c, 0x69, 0x64, 0x61, 0x74, 0x65, 0x2e, 0x70, 0x72, 0x6f,
	0x74, 0x6f, 0x22, 0x16, 0x0a, 0x14, 0x50, 0x61, 0x72, 0x74, 0x69, 0x63, 0x69, 0x70, 0x61, 0x6e,
	0x74, 0x43, 0x6f, 0x6e, 0x6e, 0x65, 0x63, 0x74, 0x65, 0x64, 0x22, 0x19, 0x0a, 0x17, 0x50, 0x61,
	0x72, 0x74, 0x69, 0x63, 0x69, 0x70, 0x61, 0x6e, 0x74, 0x44, 0x69, 0x73, 0x63, 0x6f, 0x6e, 0x6e,
	0x65, 0x63, 0x74, 0x65, 0x64, 0x22, 0xad, 0x01, 0x0a, 0x06, 0x46, 0x72, 0x65, 0x65, 0x7a, 0x65,
	0x12, 0x4a, 0x0a, 0x0a, 0x6d, 0x65, 0x64, 0x69, 0x61, 0x5f, 0x74, 0x79, 0x70, 0x65, 0x18, 0x01,
	0x20, 0x01, 0x28, 0x0e, 0x32, 0x2b, 0x2e, 0x73, 0x74, 0x72, 0x65, 0x61, 0x6d, 0x2e, 0x76, 0x69,
	0x64, 0x65, 0x6f, 0x2e, 0x63, 0x6f, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x61, 0x74, 0x6f, 0x72, 0x2e,
	0x73, 0x74, 0x61, 0x74, 0x5f, 0x76, 0x31, 0x2e, 0x4d, 0x65, 0x64, 0x69, 0x61, 0x54, 0x79, 0x70,
	0x65, 0x52, 0x09, 0x6d, 0x65, 0x64, 0x69, 0x61, 0x54, 0x79, 0x70, 0x65, 0x12, 0x20, 0x0a, 0x07,
	0x70, 0x65, 0x65, 0x72, 0x5f, 0x69, 0x64, 0x18, 0x02, 0x20, 0x01, 0x28, 0x09, 0x42, 0x07, 0xfa,
	0x42, 0x04, 0x72, 0x02, 0x10, 0x01, 0x52, 0x06, 0x70, 0x65, 0x65, 0x72, 0x49, 0x64, 0x12, 0x35,
	0x0a, 0x08, 0x64, 0x75, 0x72, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x18, 0x03, 0x20, 0x01, 0x28, 0x0b,
	0x32, 0x19, 0x2e, 0x67, 0x6f, 0x6f, 0x67, 0x6c, 0x65, 0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x62,
	0x75, 0x66, 0x2e, 0x44, 0x75, 0x72, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x52, 0x08, 0x64, 0x75, 0x72,
	0x61, 0x74, 0x69, 0x6f, 0x6e, 0x22, 0xcd, 0x02, 0x0a, 0x11, 0x4d, 0x65, 0x64, 0x69, 0x61, 0x53,
	0x74, 0x61, 0x74, 0x65, 0x43, 0x68, 0x61, 0x6e, 0x67, 0x65, 0x64, 0x12, 0x4a, 0x0a, 0x0a, 0x6d,
	0x65, 0x64, 0x69, 0x61, 0x5f, 0x74, 0x79, 0x70, 0x65, 0x18, 0x01, 0x20, 0x01, 0x28, 0x0e, 0x32,
	0x2b, 0x2e, 0x73, 0x74, 0x72, 0x65, 0x61, 0x6d, 0x2e, 0x76, 0x69, 0x64, 0x65, 0x6f, 0x2e, 0x63,
	0x6f, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x61, 0x74, 0x6f, 0x72, 0x2e, 0x73, 0x74, 0x61, 0x74, 0x5f,
	0x76, 0x31, 0x2e, 0x4d, 0x65, 0x64, 0x69, 0x61, 0x54, 0x79, 0x70, 0x65, 0x52, 0x09, 0x6d, 0x65,
	0x64, 0x69, 0x61, 0x54, 0x79, 0x70, 0x65, 0x12, 0x4a, 0x0a, 0x06, 0x63, 0x68, 0x61, 0x6e, 0x67,
	0x65, 0x18, 0x02, 0x20, 0x01, 0x28, 0x0e, 0x32, 0x32, 0x2e, 0x73, 0x74, 0x72, 0x65, 0x61, 0x6d,
	0x2e, 0x76, 0x69, 0x64, 0x65, 0x6f, 0x2e, 0x63, 0x6f, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x61, 0x74,
	0x6f, 0x72, 0x2e, 0x73, 0x74, 0x61, 0x74, 0x5f, 0x76, 0x31, 0x2e, 0x4d, 0x65, 0x64, 0x69, 0x61,
	0x53, 0x74, 0x61, 0x74, 0x65, 0x43, 0x68, 0x61, 0x6e, 0x67, 0x65, 0x52, 0x06, 0x63, 0x68, 0x61,
	0x6e, 0x67, 0x65, 0x12, 0x4e, 0x0a, 0x09, 0x64, 0x69, 0x72, 0x65, 0x63, 0x74, 0x69, 0x6f, 0x6e,
	0x18, 0x03, 0x20, 0x01, 0x28, 0x0e, 0x32, 0x30, 0x2e, 0x73, 0x74, 0x72, 0x65, 0x61, 0x6d, 0x2e,
	0x76, 0x69, 0x64, 0x65, 0x6f, 0x2e, 0x63, 0x6f, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x61, 0x74, 0x6f,
	0x72, 0x2e, 0x73, 0x74, 0x61, 0x74, 0x5f, 0x76, 0x31, 0x2e, 0x4d, 0x65, 0x64, 0x69, 0x61, 0x44,
	0x69, 0x72, 0x65, 0x63, 0x74, 0x69, 0x6f, 0x6e, 0x52, 0x09, 0x64, 0x69, 0x72, 0x65, 0x63, 0x74,
	0x69, 0x6f, 0x6e, 0x12, 0x50, 0x0a, 0x06, 0x72, 0x65, 0x61, 0x73, 0x6f, 0x6e, 0x18, 0x04, 0x20,
	0x01, 0x28, 0x0e, 0x32, 0x38, 0x2e, 0x73, 0x74, 0x72, 0x65, 0x61, 0x6d, 0x2e, 0x76, 0x69, 0x64,
	0x65, 0x6f, 0x2e, 0x63, 0x6f, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x61, 0x74, 0x6f, 0x72, 0x2e, 0x73,
	0x74, 0x61, 0x74, 0x5f, 0x76, 0x31, 0x2e, 0x4d, 0x65, 0x64, 0x69, 0x61, 0x53, 0x74, 0x61, 0x74,
	0x65, 0x43, 0x68, 0x61, 0x6e, 0x67, 0x65, 0x52, 0x65, 0x61, 0x73, 0x6f, 0x6e, 0x52, 0x06, 0x72,
	0x65, 0x61, 0x73, 0x6f, 0x6e, 0x22, 0xe5, 0x03, 0x0a, 0x0e, 0x54, 0x65, 0x6c, 0x65, 0x6d, 0x65,
	0x74, 0x72, 0x79, 0x45, 0x76, 0x65, 0x6e, 0x74, 0x12, 0x38, 0x0a, 0x09, 0x74, 0x69, 0x6d, 0x65,
	0x73, 0x74, 0x61, 0x6d, 0x70, 0x18, 0x01, 0x20, 0x01, 0x28, 0x0b, 0x32, 0x1a, 0x2e, 0x67, 0x6f,
	0x6f, 0x67, 0x6c, 0x65, 0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x62, 0x75, 0x66, 0x2e, 0x54, 0x69,
	0x6d, 0x65, 0x73, 0x74, 0x61, 0x6d, 0x70, 0x52, 0x09, 0x74, 0x69, 0x6d, 0x65, 0x73, 0x74, 0x61,
	0x6d, 0x70, 0x12, 0x6d, 0x0a, 0x15, 0x70, 0x61, 0x72, 0x74, 0x69, 0x63, 0x69, 0x70, 0x61, 0x6e,
	0x74, 0x5f, 0x63, 0x6f, 0x6e, 0x6e, 0x65, 0x63, 0x74, 0x65, 0x64, 0x18, 0x02, 0x20, 0x01, 0x28,
	0x0b, 0x32, 0x36, 0x2e, 0x73, 0x74, 0x72, 0x65, 0x61, 0x6d, 0x2e, 0x76, 0x69, 0x64, 0x65, 0x6f,
	0x2e, 0x63, 0x6f, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x61, 0x74, 0x6f, 0x72, 0x2e, 0x73, 0x74, 0x61,
	0x74, 0x5f, 0x76, 0x31, 0x2e, 0x50, 0x61, 0x72, 0x74, 0x69, 0x63, 0x69, 0x70, 0x61, 0x6e, 0x74,
	0x43, 0x6f, 0x6e, 0x6e, 0x65, 0x63, 0x74, 0x65, 0x64, 0x48, 0x00, 0x52, 0x14, 0x70, 0x61, 0x72,
	0x74, 0x69, 0x63, 0x69, 0x70, 0x61, 0x6e, 0x74, 0x43, 0x6f, 0x6e, 0x6e, 0x65, 0x63, 0x74, 0x65,
	0x64, 0x12, 0x76, 0x0a, 0x18, 0x70, 0x61, 0x72, 0x74, 0x69, 0x63, 0x69, 0x70, 0x61, 0x6e, 0x74,
	0x5f, 0x64, 0x69, 0x73, 0x63, 0x6f, 0x6e, 0x6e, 0x65, 0x63, 0x74, 0x65, 0x64, 0x18, 0x03, 0x20,
	0x01, 0x28, 0x0b, 0x32, 0x39, 0x2e, 0x73, 0x74, 0x72, 0x65, 0x61, 0x6d, 0x2e, 0x76, 0x69, 0x64,
	0x65, 0x6f, 0x2e, 0x63, 0x6f, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x61, 0x74, 0x6f, 0x72, 0x2e, 0x73,
	0x74, 0x61, 0x74, 0x5f, 0x76, 0x31, 0x2e, 0x50, 0x61, 0x72, 0x74, 0x69, 0x63, 0x69, 0x70, 0x61,
	0x6e, 0x74, 0x44, 0x69, 0x73, 0x63, 0x6f, 0x6e, 0x6e, 0x65, 0x63, 0x74, 0x65, 0x64, 0x48, 0x00,
	0x52, 0x17, 0x70, 0x61, 0x72, 0x74, 0x69, 0x63, 0x69, 0x70, 0x61, 0x6e, 0x74, 0x44, 0x69, 0x73,
	0x63, 0x6f, 0x6e, 0x6e, 0x65, 0x63, 0x74, 0x65, 0x64, 0x12, 0x65, 0x0a, 0x13, 0x6d, 0x65, 0x64,
	0x69, 0x61, 0x5f, 0x73, 0x74, 0x61, 0x74, 0x65, 0x5f, 0x63, 0x68, 0x61, 0x6e, 0x67, 0x65, 0x64,
	0x18, 0x04, 0x20, 0x01, 0x28, 0x0b, 0x32, 0x33, 0x2e, 0x73, 0x74, 0x72, 0x65, 0x61, 0x6d, 0x2e,
	0x76, 0x69, 0x64, 0x65, 0x6f, 0x2e, 0x63, 0x6f, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x61, 0x74, 0x6f,
	0x72, 0x2e, 0x73, 0x74, 0x61, 0x74, 0x5f, 0x76, 0x31, 0x2e, 0x4d, 0x65, 0x64, 0x69, 0x61, 0x53,
	0x74, 0x61, 0x74, 0x65, 0x43, 0x68, 0x61, 0x6e, 0x67, 0x65, 0x64, 0x48, 0x00, 0x52, 0x11, 0x6d,
	0x65, 0x64, 0x69, 0x61, 0x53, 0x74, 0x61, 0x74, 0x65, 0x43, 0x68, 0x61, 0x6e, 0x67, 0x65, 0x64,
	0x12, 0x42, 0x0a, 0x06, 0x66, 0x72, 0x65, 0x65, 0x7a, 0x65, 0x18, 0x05, 0x20, 0x01, 0x28, 0x0b,
	0x32, 0x28, 0x2e, 0x73, 0x74, 0x72, 0x65, 0x61, 0x6d, 0x2e, 0x76, 0x69, 0x64, 0x65, 0x6f, 0x2e,
	0x63, 0x6f, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x61, 0x74, 0x6f, 0x72, 0x2e, 0x73, 0x74, 0x61, 0x74,
	0x5f, 0x76, 0x31, 0x2e, 0x46, 0x72, 0x65, 0x65, 0x7a, 0x65, 0x48, 0x00, 0x52, 0x06, 0x66, 0x72,
	0x65, 0x65, 0x7a, 0x65, 0x42, 0x07, 0x0a, 0x05, 0x65, 0x76, 0x65, 0x6e, 0x74, 0x22, 0xad, 0x01,
	0x0a, 0x17, 0x43, 0x61, 0x6c, 0x6c, 0x50, 0x61, 0x72, 0x74, 0x69, 0x63, 0x69, 0x70, 0x61, 0x6e,
	0x74, 0x54, 0x69, 0x6d, 0x65, 0x6c, 0x69, 0x6e, 0x65, 0x12, 0x20, 0x0a, 0x07, 0x75, 0x73, 0x65,
	0x72, 0x5f, 0x69, 0x64, 0x18, 0x01, 0x20, 0x01, 0x28, 0x09, 0x42, 0x07, 0xfa, 0x42, 0x04, 0x72,
	0x02, 0x10, 0x01, 0x52, 0x06, 0x75, 0x73, 0x65, 0x72, 0x49, 0x64, 0x12, 0x26, 0x0a, 0x0a, 0x73,
	0x65, 0x73, 0x73, 0x69, 0x6f, 0x6e, 0x5f, 0x69, 0x64, 0x18, 0x02, 0x20, 0x01, 0x28, 0x09, 0x42,
	0x07, 0xfa, 0x42, 0x04, 0x72, 0x02, 0x10, 0x01, 0x52, 0x09, 0x73, 0x65, 0x73, 0x73, 0x69, 0x6f,
	0x6e, 0x49, 0x64, 0x12, 0x48, 0x0a, 0x06, 0x65, 0x76, 0x65, 0x6e, 0x74, 0x73, 0x18, 0x03, 0x20,
	0x03, 0x28, 0x0b, 0x32, 0x30, 0x2e, 0x73, 0x74, 0x72, 0x65, 0x61, 0x6d, 0x2e, 0x76, 0x69, 0x64,
	0x65, 0x6f, 0x2e, 0x63, 0x6f, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x61, 0x74, 0x6f, 0x72, 0x2e, 0x73,
	0x74, 0x61, 0x74, 0x5f, 0x76, 0x31, 0x2e, 0x54, 0x65, 0x6c, 0x65, 0x6d, 0x65, 0x74, 0x72, 0x79,
	0x45, 0x76, 0x65, 0x6e, 0x74, 0x52, 0x06, 0x65, 0x76, 0x65, 0x6e, 0x74, 0x73, 0x2a, 0x53, 0x0a,
	0x09, 0x4d, 0x65, 0x64, 0x69, 0x61, 0x54, 0x79, 0x70, 0x65, 0x12, 0x1a, 0x0a, 0x16, 0x4d, 0x45,
	0x44, 0x49, 0x41, 0x5f, 0x54, 0x59, 0x50, 0x45, 0x5f, 0x55, 0x4e, 0x53, 0x50, 0x45, 0x43, 0x49,
	0x46, 0x49, 0x45, 0x44, 0x10, 0x00, 0x12, 0x14, 0x0a, 0x10, 0x4d, 0x45, 0x44, 0x49, 0x41, 0x5f,
	0x54, 0x59, 0x50, 0x45, 0x5f, 0x41, 0x55, 0x44, 0x49, 0x4f, 0x10, 0x01, 0x12, 0x14, 0x0a, 0x10,
	0x4d, 0x45, 0x44, 0x49, 0x41, 0x5f, 0x54, 0x59, 0x50, 0x45, 0x5f, 0x56, 0x49, 0x44, 0x45, 0x4f,
	0x10, 0x02, 0x2a, 0x74, 0x0a, 0x10, 0x4d, 0x65, 0x64, 0x69, 0x61, 0x53, 0x74, 0x61, 0x74, 0x65,
	0x43, 0x68, 0x61, 0x6e, 0x67, 0x65, 0x12, 0x22, 0x0a, 0x1e, 0x4d, 0x45, 0x44, 0x49, 0x41, 0x5f,
	0x53, 0x54, 0x41, 0x54, 0x45, 0x5f, 0x43, 0x48, 0x41, 0x4e, 0x47, 0x45, 0x5f, 0x55, 0x4e, 0x53,
	0x50, 0x45, 0x43, 0x49, 0x46, 0x49, 0x45, 0x44, 0x10, 0x00, 0x12, 0x1e, 0x0a, 0x1a, 0x4d, 0x45,
	0x44, 0x49, 0x41, 0x5f, 0x53, 0x54, 0x41, 0x54, 0x45, 0x5f, 0x43, 0x48, 0x41, 0x4e, 0x47, 0x45,
	0x5f, 0x53, 0x54, 0x41, 0x52, 0x54, 0x45, 0x44, 0x10, 0x01, 0x12, 0x1c, 0x0a, 0x18, 0x4d, 0x45,
	0x44, 0x49, 0x41, 0x5f, 0x53, 0x54, 0x41, 0x54, 0x45, 0x5f, 0x43, 0x48, 0x41, 0x4e, 0x47, 0x45,
	0x5f, 0x45, 0x4e, 0x44, 0x45, 0x44, 0x10, 0x02, 0x2a, 0x68, 0x0a, 0x0e, 0x4d, 0x65, 0x64, 0x69,
	0x61, 0x44, 0x69, 0x72, 0x65, 0x63, 0x74, 0x69, 0x6f, 0x6e, 0x12, 0x1f, 0x0a, 0x1b, 0x4d, 0x45,
	0x44, 0x49, 0x41, 0x5f, 0x44, 0x49, 0x52, 0x45, 0x43, 0x54, 0x49, 0x4f, 0x4e, 0x5f, 0x55, 0x4e,
	0x53, 0x50, 0x45, 0x43, 0x49, 0x46, 0x49, 0x45, 0x44, 0x10, 0x00, 0x12, 0x18, 0x0a, 0x14, 0x4d,
	0x45, 0x44, 0x49, 0x41, 0x5f, 0x44, 0x49, 0x52, 0x45, 0x43, 0x54, 0x49, 0x4f, 0x4e, 0x5f, 0x53,
	0x45, 0x4e, 0x44, 0x10, 0x01, 0x12, 0x1b, 0x0a, 0x17, 0x4d, 0x45, 0x44, 0x49, 0x41, 0x5f, 0x44,
	0x49, 0x52, 0x45, 0x43, 0x54, 0x49, 0x4f, 0x4e, 0x5f, 0x52, 0x45, 0x43, 0x45, 0x49, 0x56, 0x45,
	0x10, 0x02, 0x2a, 0x91, 0x01, 0x0a, 0x16, 0x4d, 0x65, 0x64, 0x69, 0x61, 0x53, 0x74, 0x61, 0x74,
	0x65, 0x43, 0x68, 0x61, 0x6e, 0x67, 0x65, 0x52, 0x65, 0x61, 0x73, 0x6f, 0x6e, 0x12, 0x29, 0x0a,
	0x25, 0x4d, 0x45, 0x44, 0x49, 0x41, 0x5f, 0x53, 0x54, 0x41, 0x54, 0x45, 0x5f, 0x43, 0x48, 0x41,
	0x4e, 0x47, 0x45, 0x5f, 0x52, 0x45, 0x41, 0x53, 0x4f, 0x4e, 0x5f, 0x55, 0x4e, 0x53, 0x50, 0x45,
	0x43, 0x49, 0x46, 0x49, 0x45, 0x44, 0x10, 0x00, 0x12, 0x22, 0x0a, 0x1e, 0x4d, 0x45, 0x44, 0x49,
	0x41, 0x5f, 0x53, 0x54, 0x41, 0x54, 0x45, 0x5f, 0x43, 0x48, 0x41, 0x4e, 0x47, 0x45, 0x5f, 0x52,
	0x45, 0x41, 0x53, 0x4f, 0x4e, 0x5f, 0x4d, 0x55, 0x54, 0x45, 0x10, 0x01, 0x12, 0x28, 0x0a, 0x24,
	0x4d, 0x45, 0x44, 0x49, 0x41, 0x5f, 0x53, 0x54, 0x41, 0x54, 0x45, 0x5f, 0x43, 0x48, 0x41, 0x4e,
	0x47, 0x45, 0x5f, 0x52, 0x45, 0x41, 0x53, 0x4f, 0x4e, 0x5f, 0x43, 0x4f, 0x4e, 0x4e, 0x45, 0x43,
	0x54, 0x49, 0x4f, 0x4e, 0x10, 0x02, 0x42, 0x31, 0x42, 0x06, 0x53, 0x74, 0x61, 0x74, 0x56, 0x31,
	0x50, 0x01, 0x5a, 0x07, 0x73, 0x74, 0x61, 0x74, 0x5f, 0x76, 0x31, 0xaa, 0x02, 0x1b, 0x53, 0x74,
	0x72, 0x65, 0x61, 0x6d, 0x2e, 0x56, 0x69, 0x64, 0x65, 0x6f, 0x2e, 0x76, 0x31, 0x2e, 0x43, 0x6f,
	0x6f, 0x72, 0x64, 0x69, 0x6e, 0x61, 0x74, 0x6f, 0x72, 0x62, 0x06, 0x70, 0x72, 0x6f, 0x74, 0x6f,
	0x33,
}

var (
	file_video_coordinator_stat_v1_stat_proto_rawDescOnce sync.Once
	file_video_coordinator_stat_v1_stat_proto_rawDescData = file_video_coordinator_stat_v1_stat_proto_rawDesc
)

func file_video_coordinator_stat_v1_stat_proto_rawDescGZIP() []byte {
	file_video_coordinator_stat_v1_stat_proto_rawDescOnce.Do(func() {
		file_video_coordinator_stat_v1_stat_proto_rawDescData = protoimpl.X.CompressGZIP(file_video_coordinator_stat_v1_stat_proto_rawDescData)
	})
	return file_video_coordinator_stat_v1_stat_proto_rawDescData
}

var file_video_coordinator_stat_v1_stat_proto_enumTypes = make([]protoimpl.EnumInfo, 4)
var file_video_coordinator_stat_v1_stat_proto_msgTypes = make([]protoimpl.MessageInfo, 6)
var file_video_coordinator_stat_v1_stat_proto_goTypes = []interface{}{
	(MediaType)(0),                  // 0: stream.video.coordinator.stat_v1.MediaType
	(MediaStateChange)(0),           // 1: stream.video.coordinator.stat_v1.MediaStateChange
	(MediaDirection)(0),             // 2: stream.video.coordinator.stat_v1.MediaDirection
	(MediaStateChangeReason)(0),     // 3: stream.video.coordinator.stat_v1.MediaStateChangeReason
	(*ParticipantConnected)(nil),    // 4: stream.video.coordinator.stat_v1.ParticipantConnected
	(*ParticipantDisconnected)(nil), // 5: stream.video.coordinator.stat_v1.ParticipantDisconnected
	(*Freeze)(nil),                  // 6: stream.video.coordinator.stat_v1.Freeze
	(*MediaStateChanged)(nil),       // 7: stream.video.coordinator.stat_v1.MediaStateChanged
	(*TelemetryEvent)(nil),          // 8: stream.video.coordinator.stat_v1.TelemetryEvent
	(*CallParticipantTimeline)(nil), // 9: stream.video.coordinator.stat_v1.CallParticipantTimeline
	(*durationpb.Duration)(nil),     // 10: google.protobuf.Duration
	(*timestamppb.Timestamp)(nil),   // 11: google.protobuf.Timestamp
}
var file_video_coordinator_stat_v1_stat_proto_depIdxs = []int32{
	0,  // 0: stream.video.coordinator.stat_v1.Freeze.media_type:type_name -> stream.video.coordinator.stat_v1.MediaType
	10, // 1: stream.video.coordinator.stat_v1.Freeze.duration:type_name -> google.protobuf.Duration
	0,  // 2: stream.video.coordinator.stat_v1.MediaStateChanged.media_type:type_name -> stream.video.coordinator.stat_v1.MediaType
	1,  // 3: stream.video.coordinator.stat_v1.MediaStateChanged.change:type_name -> stream.video.coordinator.stat_v1.MediaStateChange
	2,  // 4: stream.video.coordinator.stat_v1.MediaStateChanged.direction:type_name -> stream.video.coordinator.stat_v1.MediaDirection
	3,  // 5: stream.video.coordinator.stat_v1.MediaStateChanged.reason:type_name -> stream.video.coordinator.stat_v1.MediaStateChangeReason
	11, // 6: stream.video.coordinator.stat_v1.TelemetryEvent.timestamp:type_name -> google.protobuf.Timestamp
	4,  // 7: stream.video.coordinator.stat_v1.TelemetryEvent.participant_connected:type_name -> stream.video.coordinator.stat_v1.ParticipantConnected
	5,  // 8: stream.video.coordinator.stat_v1.TelemetryEvent.participant_disconnected:type_name -> stream.video.coordinator.stat_v1.ParticipantDisconnected
	7,  // 9: stream.video.coordinator.stat_v1.TelemetryEvent.media_state_changed:type_name -> stream.video.coordinator.stat_v1.MediaStateChanged
	6,  // 10: stream.video.coordinator.stat_v1.TelemetryEvent.freeze:type_name -> stream.video.coordinator.stat_v1.Freeze
	8,  // 11: stream.video.coordinator.stat_v1.CallParticipantTimeline.events:type_name -> stream.video.coordinator.stat_v1.TelemetryEvent
	12, // [12:12] is the sub-list for method output_type
	12, // [12:12] is the sub-list for method input_type
	12, // [12:12] is the sub-list for extension type_name
	12, // [12:12] is the sub-list for extension extendee
	0,  // [0:12] is the sub-list for field type_name
}

func init() { file_video_coordinator_stat_v1_stat_proto_init() }
func file_video_coordinator_stat_v1_stat_proto_init() {
	if File_video_coordinator_stat_v1_stat_proto != nil {
		return
	}
	if !protoimpl.UnsafeEnabled {
		file_video_coordinator_stat_v1_stat_proto_msgTypes[0].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*ParticipantConnected); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_video_coordinator_stat_v1_stat_proto_msgTypes[1].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*ParticipantDisconnected); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_video_coordinator_stat_v1_stat_proto_msgTypes[2].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*Freeze); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_video_coordinator_stat_v1_stat_proto_msgTypes[3].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*MediaStateChanged); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_video_coordinator_stat_v1_stat_proto_msgTypes[4].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*TelemetryEvent); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_video_coordinator_stat_v1_stat_proto_msgTypes[5].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*CallParticipantTimeline); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
	}
	file_video_coordinator_stat_v1_stat_proto_msgTypes[4].OneofWrappers = []interface{}{
		(*TelemetryEvent_ParticipantConnected)(nil),
		(*TelemetryEvent_ParticipantDisconnected)(nil),
		(*TelemetryEvent_MediaStateChanged)(nil),
		(*TelemetryEvent_Freeze)(nil),
	}
	type x struct{}
	out := protoimpl.TypeBuilder{
		File: protoimpl.DescBuilder{
			GoPackagePath: reflect.TypeOf(x{}).PkgPath(),
			RawDescriptor: file_video_coordinator_stat_v1_stat_proto_rawDesc,
			NumEnums:      4,
			NumMessages:   6,
			NumExtensions: 0,
			NumServices:   0,
		},
		GoTypes:           file_video_coordinator_stat_v1_stat_proto_goTypes,
		DependencyIndexes: file_video_coordinator_stat_v1_stat_proto_depIdxs,
		EnumInfos:         file_video_coordinator_stat_v1_stat_proto_enumTypes,
		MessageInfos:      file_video_coordinator_stat_v1_stat_proto_msgTypes,
	}.Build()
	File_video_coordinator_stat_v1_stat_proto = out.File
	file_video_coordinator_stat_v1_stat_proto_rawDesc = nil
	file_video_coordinator_stat_v1_stat_proto_goTypes = nil
	file_video_coordinator_stat_v1_stat_proto_depIdxs = nil
}
