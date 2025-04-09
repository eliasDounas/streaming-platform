import LiveCard from "./LiveCardItem";

const mockLiveData = Array.from({ length: 12 }, (_, i) => ({
  id: i,
}));

const LiveCardList = () => {
  return (
    <section className="px-4 py-6">
      <h2 className="text-2xl font-semibold mb-6">Live Now</h2>

      <div className="grid gap-x-6 gap-y-8 grid-cols-[repeat(auto-fill,minmax(350px,1fr))]"
          style={{ gridTemplateColumns: "repeat(auto-fit, minmax(250px, 1fr))" }}>
        {mockLiveData.map((stream) => (
          <LiveCard key={stream.id} />
        ))}
      </div>
    </section>
  );
};

export default LiveCardList;
